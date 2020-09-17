package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:spring-rabbitmq-producer.xml")
public class ProducerTest {

    @Autowired
    // 注入RabbitTemplate
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testHelloWorld(){
        rabbitTemplate.convertAndSend("spring_queue", "hello world spring!");
    }

    @Test
    public void testFanout(){
        rabbitTemplate.convertAndSend("spring_fanout_exchange", "", "spring fanout...");
    }

    @Test
    public void testTopics(){
        rabbitTemplate.convertAndSend("spring_topics_exchange", "heima.ha.haha", "spring topics...");
    }

    @Test
    // 测试确认模式
    /*
    *当消息发送给exchange时，会返回确认信息
    * 步骤：
    *   1、开启确认模式，publisher-confirms="true"
    *   2、在rabbitTemplate中定义ConfirmCallback回调函数
    * */
    public void testConfirm(){
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            // 定义回调方法
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                /*
                参数：
                    correlationData: 相关配置信息
                    b: 交换机是否接受到了消息，true为成功
                    s: 若失败，则给出失败的原因
                * */
                System.out.println("消息已经确认");
            }
        });
        rabbitTemplate.convertAndSend("spring_topics_exchange", "heima.ha.haha", "spring topics...");
    }

    @Test
    // 测试回退模式
    /*
    * 当消息发送给ExChange以后，ExChange路由到Queue失败之后，才会执行名为ReturnCallback的回调方法
    * 步骤：
    *   1、开启回退模式，publisher-returns="true"
    *   2、设置ReturnCallback，
    *   3、设置ExChange处理消息的模式
    *       1、如果消息没有路由到Queue，则丢弃，为默认模式
    *       2、如果消息没有路由到Queue，则回调ReturnCallback，需要手动设置
    * */
    public void testReturn(){
        // 设置交换机处理失败消息的模式
        rabbitTemplate.setMandatory(true);

        // 2、设置ReturnCallback
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             *
             * @param message 消息对象
             * @param replyCode 错误码
             * @param replyText 错误消息
             * @param exchange 交换机
             * @param routingKey 路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("return执行了 ");
            }

        });
        //  3、发送消息
        rabbitTemplate.convertAndSend("spring_topics_exchange", "heima2222.ha.haha", "spring topics...");
    }
}
