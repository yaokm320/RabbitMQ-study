package com.example.consumer;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConsumerWorkQueues2 {
    public static void main(String[] args) throws IOException, TimeoutException {

        // 1、创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();

        // 2、设置参数=
        connectionFactory.setHost("localhost");         // 设置连接IP地址，默认值为localhost
        connectionFactory.setPort(5672);                // 设置端口，默认值是5672
        connectionFactory.setVirtualHost("/example");   // 设置默认的虚拟机
        connectionFactory.setUsername("root");          // 设置用户名
        connectionFactory.setPassword("root");          // 设置默吗

        // 3、创建连接connection
        Connection connection = connectionFactory.newConnection();

        // 4、创建channel
        Channel channel = connection.createChannel();

        // 5、创建队列queue,如果没有名字叫"hello world"的队列，则会创建该队列，否则不会创建
        /*
         * queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
         * 参数：
         *  queue：队列名称
         *  durable：是否持久化，当mq重启之后，还在
         *  exclusive：是否独占，只能有一个消费者监听队列，当connectin关闭时，是否删除队列
         *  autoDelete：是否自动删除，当没有consumer时，会自动删除
         *  arguments：参数
         * */
        channel.queueDeclare("work queues", true, false, false, null);

        // 6、获取消息
        /*
         * basicConsume(String queue, boolean autoAck, Consumer callback)
         * 参数：
         *  queue: 参数的名称
         *  autoAck: 是否自动确认，得到消息之后，消费者自动确认
         *  callback: 回调对象
         *
        * */
        Consumer consumer = new DefaultConsumer(channel){
            /*
            * 回调方法：当收到消息后，会自动执行该方法
            * 1、 consumerTag: 标识
            * 2、 envelope：获取信息，交换机，路由key
            * 3、 properties：配置信息
            * 4、 body：数据
            * */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                System.out.println("body:\n"+new String(body));
            }
        };
        channel.basicConsume("work queues", true, consumer);

        // 消费者不需要关闭资源

    }
}
