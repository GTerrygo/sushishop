package com.terry.sushishop.util;

import com.terry.sushishop.entity.Order;
import com.terry.sushishop.entity.Sushi;
import com.terry.sushishop.service.OrderService;
import com.terry.sushishop.service.SushiService;
import net.bytebuddy.agent.builder.AgentBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author
 * @create 2022-01-23-18:20
 */
public class ChefExecutorPool {
    //chef threads
    private final ArrayList<Mthread> mthreads;

    //order task queue
    private final BlockingQueue<OrderTask> taskQueue;

    //the size of chefs
    private final int threadNum;

    //the size of chef who is working
    private int workThreadNum;

    //if close the pool
    private volatile boolean isShutDown;

    //lock
    private final ReentrantLock mainLock = new ReentrantLock();

    public ChefExecutorPool(int threadNum) {
        mthreads = new ArrayList<>(threadNum);
        taskQueue = new ArrayBlockingQueue<>(100);
        this.threadNum = threadNum;
        this.workThreadNum = 0;
    }

    public void stopChefByOrderId(Integer orderId){
        mthreads.forEach((thread)->{
            if (thread.getOrderTask() != null && thread.getOrderTask().getOrder().getId() == orderId) {
                synchronized (thread) {
                    thread.notifyAll();
                    thread.getOrderTask().getOrder().setStatusId(5);
                }
            }
        });
    }
    public void pauseChefByOrderId(Integer orderId){
        mthreads.forEach((thread)->{
            if(thread.getOrderTask() != null && thread.getOrderTask().getOrder().getId() == orderId){
                thread.getOrderTask().getOrder().setStatusId(3);
            }
        });
    }
    public void resumeChefByOrderId(Integer orderId){
        mthreads.forEach((thread)->{
            if(thread.getOrderTask() != null && thread.getOrderTask().getOrder().getId() == orderId){
                synchronized (thread){
                    thread.getOrderTask().getOrder().setStatusId(2);
                    thread.notifyAll();
                }
            }
        });
    }


    public OrderTask getProcessingOrderTaskByOrderId(Integer orderId){
        for(int i = 0; i<mthreads.size(); i++){
            Mthread thread = mthreads.get(i);
            if(thread.getOrderTask() != null && thread.getOrderTask().getOrder().getId() == orderId){
                return thread.getOrderTask();
            }
        }
        return null;
    }
    public void execute(OrderTask task) {
        if (isShutDown) {
            throw new RuntimeException("The pool has been shut down");
        }
        try {
            mainLock.lock();
            //如果工作线程小于总线程数，就开启线程
            if (workThreadNum < threadNum) {
                Mthread thread = new Mthread(task);
                thread.start();
                mthreads.add(thread);//防止gc回收掉
                workThreadNum++;
            }else {
                //添加到任务队列
                if (!taskQueue.offer(task)){
                    System.out.println("queue has been full");
                }
            }

        } finally {
            mainLock.unlock();
        }
    }

    void shutDown(){
        for (Mthread mthread : mthreads) {
            mthread.interrupt();
        }
        isShutDown=true;
    }

    class Mthread extends Thread {
        OrderTask task;
        public Mthread(OrderTask task) {
            this.task = task;
        }
        public OrderTask getOrderTask(){
            return this.task;
        }
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()){
                if (task!=null){
                    task.run();
                    task=null;
                }else {
                    //当前线程处理完自己的了，就会处理队列中的任务
                    task = taskQueue.poll();
                    if (task!=null){
                        task.run();
                        task = null;
                    }
                }
            }
        }
    }

    public static class OrderTask implements Runnable {
        private Order order;
        private OrderService orderService;
        private SushiService sushiService;
        private long start;
        private long totalPauseTime;
        public OrderTask(Order order, OrderService orderService, SushiService sushiService){
            this.order = order;
            this.orderService = orderService;
            this.sushiService = sushiService;
            totalPauseTime = 0;
        }
        @Override
        public void run() {
            //update order status - in-progress
            order.setStatusId(2);
            orderService.updateOrder(order);
            //check the time to make for the sushi in the order
            Integer sushiId = order.getSushiId();
            Sushi sushi = sushiService.findSushiById(sushiId);
            long time = sushi.getTimeToMake() * 1000;

            start = new Date().getTime();
            long end = new Date().getTime();
            //simulating sushi cooking process
            synchronized (Thread.currentThread()){
                while (end-start < time && !Thread.currentThread().isInterrupted()){
                    switch (order.getStatusId()){
                        case 3://pause
                            try {
                                long pauseStart = new Date().getTime();
                                Thread.currentThread().wait();
                                long pauseEnd = new Date().getTime();
                                time += pauseEnd - pauseStart;
                                totalPauseTime += pauseEnd - pauseStart;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            break;
                        case 5://stop
                            return;
                        default:
                            break;
                    }
                    end = new Date().getTime();
                }
            }
            //finish the order
            order.setStatusId(4);
            order.setTimeSpent( (end - start - totalPauseTime) / 1000);
            orderService.updateOrder(order);
        }

        public Order getOrder() {
            return order;
        }

        public long getStart(){
            return start;
        }

        public long getTotalPauseTime(){ return  totalPauseTime; }
    }
}
