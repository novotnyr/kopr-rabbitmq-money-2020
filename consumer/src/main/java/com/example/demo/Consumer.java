package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    public static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @RabbitListener(queues = "payment.eur")
    public void consume(Payment payment) {
        logger.info("{}", payment.getAmount());

        if (payment.getAmount() > 300) {
            throw new IllegalStateException("Cannot process such payments");
        }
    }


}
