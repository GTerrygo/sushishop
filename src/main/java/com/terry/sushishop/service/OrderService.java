package com.terry.sushishop.service;

import com.terry.sushishop.entity.Order;
import com.terry.sushishop.entity.Status;
import com.terry.sushishop.repository.OrderRepository;
import com.terry.sushishop.repository.StatusRepository;
import com.terry.sushishop.util.ChefExecutorPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

/**
 * @author
 * @create 2022-01-22-23:52
 */
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StatusRepository statusRepository;

    public Order createOrder(Integer id){
        Order order = new Order();
        order.setSushiId(id);
        order.setStatusId(1);
        order.setCreatedAt(new Date());
        order.setTimeSpent((long)0);
        Order save = orderRepository.save(order);
        return save;
    }
    public boolean cancelOrder(Integer id, ChefExecutorPool.OrderTask orderTask){
        if(orderTask == null ) return false;
        Order order = orderTask.getOrder();
        order = order == null ? findOrderById(id) : order;
        if(order == null || order.getStatusId() == 4) return false;
        order.setStatusId(5);
        long start = orderTask.getStart();
        long totalPauseTime = orderTask.getTotalPauseTime();
        order.setTimeSpent((new Date().getTime() - start - totalPauseTime)/1000);
        updateOrder(order);
        return true;
    }

    public boolean pauseOrder(Integer id, ChefExecutorPool.OrderTask orderTask){
        if(orderTask == null ) return false;
        Order order = orderTask.getOrder();
        order = order == null ? findOrderById(id) : order;
        if(order == null || order.getStatusId() != 2) return false;
        order.setStatusId(3);
        long start = orderTask.getStart();
        long totalPauseTime = orderTask.getTotalPauseTime();
        order.setTimeSpent((new Date().getTime() - start - totalPauseTime)/1000);
        updateOrder(order);
        return  true;
    }

    public boolean resumeOrder(Integer id){
        Order order = findOrderById(id);
        if(order == null || order.getStatusId() != 3) return false;
        order.setStatusId(2);
        updateOrder(order);
        return true;
    }
    public Order findOrderById(Integer id){
        Optional<Order> optionalOrder = orderRepository.findById(id);
        return optionalOrder.orElse(null);
    }

    public HashMap<String, ArrayList<Order>> getStatusAndOrdersMap(ChefExecutorPool chefExecutorPool){
        List<Order> allOrders = orderRepository.findAll();
        HashMap<String, ArrayList<Order>> statusAndOrdersMap = new HashMap<String, ArrayList<Order>>();
        //create status map and fill element in statusAndOrdersMap
        List<Status> allStatus = statusRepository.findAll();
        HashMap<Integer, String> statusMap = new HashMap<>();
        allStatus.forEach(status -> {
            statusMap.put(status.getId(), status.getName());
            statusAndOrdersMap.put(status.getName(), new ArrayList<Order>());
        });

        allOrders.forEach((order)->{
            if(order.getStatusId() == 2){
                ChefExecutorPool.OrderTask processingOrderTask = chefExecutorPool.getProcessingOrderTaskByOrderId(order.getId());
                if(processingOrderTask == null) return;
                long totalPauseTime = processingOrderTask.getTotalPauseTime();
                long start = processingOrderTask.getStart();
                long current = new Date().getTime();
                order.setTimeSpent((current - start - totalPauseTime)/1000);
            }
            String statusName = statusMap.get(order.getStatusId());
            ArrayList<Order> orders = statusAndOrdersMap.get(statusName);
            order.setCreatedAt(null);
            order.setSushiId(null);
            order.setStatusId(null);
            orders.add(order);
        });

        return statusAndOrdersMap;
    }
    public void updateOrder(Order order){
        orderRepository.save(order);
    }
}
