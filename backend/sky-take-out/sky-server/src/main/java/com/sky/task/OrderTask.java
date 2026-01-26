package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单任务类
 */
@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单 每分钟触发一次
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processTimeoutOrder(){
        log.info("处理超时订单 每分钟触发一次 {}", LocalDateTime.now().toString());

        // slect * from orders where status = ? and order_time < (当前时间-15分钟)
        // 超时时间为15分钟
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(15);
        // 处理超时订单
        List<Orders> orderList = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, timeoutTime);
        // 判断一下取出来的订单列表是否为空
        if(orderList != null && orderList.size() > 0){
            // 遍历订单列表，将超时订单状态修改为已取消
            orderList.forEach(order -> {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("订单超时 自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            });
        }

    }

    /**
     * 处理一直在派送中的订单 每天凌晨一点触发一次
     */
    @Scheduled(cron = "0 0 1 * * ?")    // 每天凌晨一点触发一次
    public void processOrder(){
        log.info("处理一直在派送中的订单 每天凌晨一点触发一次 {}", LocalDateTime.now().toString());

        // 查出订单列表 中前一天十二点的订单
        LocalDateTime orderTime = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, orderTime);

        // 判断一下取出来的订单列表是否为空
        if(ordersList != null && ordersList.size() > 0){
            // 遍历订单列表，将超时订单状态修改为已取消
            ordersList.forEach(order -> {
                order.setStatus(Orders.COMPLETED);
                order.setCancelReason("过了一天 订单已完成");
                orderMapper.update(order);
            });
        }

    }

}
