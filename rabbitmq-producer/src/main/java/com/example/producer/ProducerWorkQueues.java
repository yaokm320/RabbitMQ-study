package com.example.producer;


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/*
* 发送消息
* */
public class ProducerWorkQueues {
    public static void main(String[] args) throws IOException, TimeoutException {

        // 1、创建连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();

        // 2、设置参数
        connectionFactory.setHost("localhost");         // 设置连接IP地址，默认值为localhost
        connectionFactory.setPort(5672);                // 设置端口，默认值是5672
        connectionFactory.setVirtualHost("/example");   // 设置默认的虚拟机
        connectionFactory.setUsername("root");          // 设置用户名
        connectionFactory.setPassword("root");          // 设置默吗

        // 3、创建连接connection
        Connection connection = connectionFactory.newConnection();

        // 4、创建channel
        Channel channel = connection.createChannel();

        // 5、创建队列queue,如果没有名字叫"work queues"的队列，则会自动创建该队列
        channel.queueDeclare("work queues", true, false, false, null);
        /*
         * queueDeclare(String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
         * 参数：
         *   queue：队列名称
         *   durable：是否持久化，当mq重启之后，还在
         *   exclusive：是否独占，只能有一个消费者监听队列，当connection关闭时，是否删除队列
         *   autoDelete：是否自动删除，当没有consumer时，会自动删除
         *   arguments：参数
         * */

        // 6、发送消息,一次性发送十条消息
        for (int i = 0; i < 10; i++) {
            String body = "hello rabbitmq " + i;
            channel.basicPublish("", "work queues", null, body.getBytes());
            /*
             * basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
             * 参数：
             *   exchange:交换机的名称，简单模式下，会使用默认的交换机，默认交换机的名称为""
             *   routingKey：路由键
             *   props：配置信息
             *   body：发送消息数据
             * */
        }

        // 7、释放资源
        channel.close();
        connection.close();
    }
}
