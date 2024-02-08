package com.mpedroni.runthebank.infra.services.impl;

import com.mpedroni.runthebank.infra.services.EventService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqEventService implements EventService {
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqEventService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void send(Object message, String target) {
        try {
            rabbitTemplate.convertAndSend(
                "transaction.events",
                target,
                message
            );
        } catch (Exception e) {
            System.out.println("Failed to enqueue the message");
        }
    }
}
