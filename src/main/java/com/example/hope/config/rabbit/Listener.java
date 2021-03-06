package com.example.hope.config.rabbit;

import cn.hutool.json.JSONUtil;
import com.example.hope.model.entity.Orders;
import com.example.hope.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Listener {

    private WebSocketService webSocketService;

    @Autowired
    public Listener(WebSocketService webSocketService) {
        this.webSocketService = webSocketService;
    }

    // 收消息
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order.created", durable = "true"),
            exchange = @Exchange(
                    value = "order.exchange",
                    ignoreDeclarationExceptions = "true", // 忽略声明异常
                    type = ExchangeTypes.TOPIC // 交换机类型
            ),
            key = "order.created"
    ))
    public void onOrderCreated(Orders order) {
        log.info("create order ->:{}", order.getId());
        webSocketService.sendMessage(JSONUtil.toJsonStr(order));
    }
}
