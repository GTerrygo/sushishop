package com.terry.sushishop.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.terry.sushishop.entity.Order;

/**
 * @author
 * @create 2022-01-23-1:11
 */
public class CommonResult<T> {
    public static final String ORDER_CREATED="Order created";
    public static final String SUSHI_NO_EXIST="Sushi no exist";
    public static final String ORDER_CANCELLED="Order cancelled";
    public static final String ORDER_PAUSED="Order paused";
    public static final String ORDER_RESUMED="Order resumed";
    public static final String ORDER_STATUS_ERROR="Order status error";
    private Integer code;
    private String msg;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Order order;

    public static CommonResult result(Integer code, String msg){
        CommonResult result = new CommonResult();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

    public static CommonResult result(Integer code, String msg, Order order){
        CommonResult result = new CommonResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setOrder(order);
        return result;
    }
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
