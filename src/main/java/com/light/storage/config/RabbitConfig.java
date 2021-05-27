package com.light.storage.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Autowired
    CachingConnectionFactory cachingConnectionFactory;


    @Bean
    RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(cachingConnectionFactory);
        rabbitTemplate.setConfirmCallback((data, ack, cause) -> {
            String msgId = data.getId();
            if (ack) {
                System.out.println(msgId + ":消息发送成功");
//                mailSendLogService.updateMailSendLogStatus(msgId, 1);//修改数据库中的记录，消息投递成功
            } else {
                System.out.println(msgId + ":消息发送失败");
            }
        });
        rabbitTemplate.setReturnCallback((msg, repCode, repText, exchange, routingkey) -> {
            System.out.println("消息发送失败");
        });
        return rabbitTemplate;
    }

    @Bean
    Queue mailQueue() {
        return new Queue("mail.insert", true);
    }

    @Bean
    Queue mailQueue1() {
        return new Queue("mail.findPass", true);
    }
 @Bean
    Queue mailQueue2() {
        return new Queue("mail.birth", true);
    }

    @Bean
    DirectExchange mailExchange() {
        return new DirectExchange("mail", true, false);
    }


    @Bean
    Binding mailBinding() {
        return BindingBuilder.bind(mailQueue()).to(mailExchange()).with("insert");
    }
    @Bean
    Binding mailBinding1() {
        return BindingBuilder.bind(mailQueue1()).to(mailExchange()).with("findPass");
    }
    @Bean
    Binding mailBinding2() {
        return BindingBuilder.bind(mailQueue2()).to(mailExchange()).with("birth");
    }

}
