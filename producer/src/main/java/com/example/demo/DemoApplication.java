package com.example.demo;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class DemoApplication {
	@Autowired
	private RabbitTemplate rabbitTemplate;

	@Value("${merchant:unknown}")
	private String merchant;

	@Scheduled(fixedDelay = 10000)
	public void payMoney() {
		Payment payment = Payment.randomPayment();
		String routingKey = this.merchant + "." + payment.getCurrency().toLowerCase();
		rabbitTemplate.convertAndSend("payment", routingKey, payment);
	}

	@Bean
	TopicExchange paymentExchange() {
		return new TopicExchange("payment");
	}

	@Bean
	MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
