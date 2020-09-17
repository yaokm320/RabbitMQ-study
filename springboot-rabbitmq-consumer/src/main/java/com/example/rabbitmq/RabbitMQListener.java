package com.example.rabbitmq;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {

    @RabbitListener(queues = "springboot_topics_queue")
    public void listenerQueue(Message message){
        System.out.println(new String(message.getBody()));
    }
}

