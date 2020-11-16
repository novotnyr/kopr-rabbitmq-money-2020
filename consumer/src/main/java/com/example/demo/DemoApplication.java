package com.example.demo;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {
	@Bean
	Queue queue() {
		return new Queue("payment.eur");
	}

	@Bean
	Exchange exchange() {
		return new TopicExchange("payment");
	}

	@Bean
	Binding binding() {
		return BindingBuilder.bind(queue()).to(exchange()).with("#.EUR").noargs();
	}

	@Bean
	MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
		var c = new SimpleMessageListenerContainer(connectionFactory);
		c.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		c.addQueues(queue());
		c.setMessageListener(new ChannelAwareMessageListener() {
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				System.out.println(new String(message.getBody()));
				if (Math.random() < 0.7) {
					System.out.println("Rejected");
					channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
				} else {
					System.out.println("Accepted");
					channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
				}
			}
		});
		return c;
	}

	@Bean
	@ConditionalOnProperty("reliable")
	MessageListenerContainer reliableMessageListener(ConnectionFactory connectionFactory) {
		var c = new SimpleMessageListenerContainer(connectionFactory);
		c.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		c.addQueues(queue());
		c.setMessageListener(new ChannelAwareMessageListener() {
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				System.out.println(new String(message.getBody()));
				System.out.println("Accepted");
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			}
		});
		return c;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
