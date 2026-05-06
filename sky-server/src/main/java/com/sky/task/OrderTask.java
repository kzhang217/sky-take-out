package com.sky.task;


import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    OrderMapper orderMapper;


    @Scheduled(cron = "0 * * * * ?")
    //@Scheduled(cron = "0/5 * * * * ?")
    public void processTimeOutOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        List<Orders> orders=orderMapper.getByStatusAndTimeLT(Orders.PENDING_PAYMENT, LocalDateTime.now().plusMinutes(-15));

        if(orders != null && orders.size()>0){
            for(Orders order:orders){
                order. setStatus(Orders.CANCELLED);
                order.setCancelReason("超时");
                orderMapper.update(order);
            }
        }
    }

    @Scheduled(cron = "0 0 1 * * ?")
    //@Scheduled(cron = "1/5 * * * * ?")
    public void processDeliveryOrder(){
        log.info("定时处理超时订单：{}", LocalDateTime.now());
        List<Orders> orders=orderMapper.getByStatusAndTimeLT(Orders.DELIVERY_IN_PROGRESS, LocalDateTime.now().plusMinutes(-60));
        if(orders != null && orders.size()>0){
            for(Orders order:orders){
                order. setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }


    }
}
