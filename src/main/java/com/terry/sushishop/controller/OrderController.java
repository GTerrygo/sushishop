package com.terry.sushishop.controller;

import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.terry.sushishop.util.ChefExecutorPool;
import com.terry.sushishop.util.CommonResult;
import com.terry.sushishop.entity.Order;
import com.terry.sushishop.entity.Sushi;
import com.terry.sushishop.service.OrderService;
import com.terry.sushishop.service.SushiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author
 * @create 2022-01-22-23:54
 */
@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private SushiService sushiService;
    @Autowired
    private ChefExecutorPool chefExecutorPool;

    @PostMapping("/api/orders")
    public CommonResult createOrder(@RequestBody Map map) throws CloneNotSupportedException {
        String sushiName = (String) map.get("sushi_name");
        Sushi sushi = sushiService.findSushiByName(sushiName);
        //check the sushi if exist
        if(sushi == null)
            return CommonResult.result(300, CommonResult.SUSHI_NO_EXIST, null);
        //create order
        Order order = orderService.createOrder(sushi.getId());
        //put the order in a waiting queue
        chefExecutorPool.execute(new ChefExecutorPool.OrderTask(order, orderService, sushiService));
        //hide timeSpent field
        Order clone = (Order)order.clone();
        clone.setTimeSpent(null);
        return CommonResult.result(0, CommonResult.ORDER_CREATED, clone);
    }

    @DeleteMapping("/api/orders/{order_id}")
    public CommonResult cancelOrder(@PathVariable Integer order_id){
        //ask chef to get the order start time and update order status in database - cancelled
        ChefExecutorPool.OrderTask processingOrderTaskByOrder = chefExecutorPool.getProcessingOrderTaskByOrderId(order_id);
        boolean success = orderService.cancelOrder(order_id, processingOrderTaskByOrder);
        if(!success) return CommonResult.result(300, CommonResult.ORDER_STATUS_ERROR);
        //stop chef work if someone is cooking the order
        chefExecutorPool.stopChefByOrderId(order_id);
        return CommonResult.result(0, CommonResult.ORDER_CANCELLED);
    }

    @PutMapping("/api/orders/{order_id}/pause")
    public CommonResult pauseOrder(@PathVariable Integer order_id){
        //ask chef to get the order start time and update order status in database - paused
        ChefExecutorPool.OrderTask processingOrderTaskByOrder = chefExecutorPool.getProcessingOrderTaskByOrderId(order_id);
        boolean success = orderService.pauseOrder(order_id, processingOrderTaskByOrder);
        if(!success) return CommonResult.result(300, CommonResult.ORDER_STATUS_ERROR);
        //inform chef pause the order;
        chefExecutorPool.pauseChefByOrderId(order_id);
        return CommonResult.result(0, CommonResult.ORDER_PAUSED);
    }

    @PutMapping("/api/orders/{order_id}/resume")
    public CommonResult resumeOrder(@PathVariable Integer order_id){
        //update order status in database - resumed
        boolean success = orderService.resumeOrder(order_id);
        if(!success) return CommonResult.result(300, CommonResult.ORDER_STATUS_ERROR);
        //inform chef resume the order;
        chefExecutorPool.resumeChefByOrderId(order_id);
        return CommonResult.result(0, CommonResult.ORDER_RESUMED);
    }

    @GetMapping("/api/orders/status")
    public Map<String, ArrayList<Order>> getAllOrdersStatus(){
        //get All orders from database
        HashMap<String, ArrayList<Order>> statusAndOrdersMap = orderService.getStatusAndOrdersMap(chefExecutorPool);
        return statusAndOrdersMap;
    }
}
