package com.example.producer;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


/*
* 发送消息
* */
public class ProducerTopics {
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

        // 5、创建交换机
        String exchangeName = "test_topics_exchange";
        channel.exchangeDeclare(exchangeName, BuiltinExchangeType.TOPIC, true, false, false,null);
        /*
        * exchangeDeclare(String exchange, BuiltinExchangeType type, boolean durable, boolean autoDelete, boolean internal, Map<String, Object> arguments)
        * 参数：
        *   exchange：交换机名称
        *   type：交换机的类型
                DIRECT("direct")：定向
                FANOUT("fanout")：扇形，发送消息到与之绑定的所有的队列
                TOPIC("topic")： 通配符
                HEADERS("headers")： 参数匹配
        *   durable： 持久化
        *   autoDelete： 自动删除
        *   internal： 内部使用，一般是false
        *   arguments： 参数
        * */

        // 6、创建两个队列
        String queue1Name = "test_topics_queue1";
        String queue2Name = "test_topics_queue2";
        channel.queueDeclare(queue1Name, true, false, false, null);
        channel.queueDeclare(queue2Name, true, false, false, null);

        // 7、绑定队列和交换机
        /*
         * routingKey 系统的名称.日志的级别
         * 需求：
         *   所有的error级别的日志存入数据库，所有order系统的数据存入数据库
         *
         * */
        // 以error结尾和以order开头的推送到queue1里面去。
        channel.queueBind(queue1Name, exchangeName, "#.error");   // # 匹配0个或多个
        channel.queueBind(queue1Name, exchangeName, "order.*");   // * 匹配一个
        // 所有的都发送到queue2里面去
        channel.queueBind(queue2Name, exchangeName, "*.*");   // * 匹配一个
        /*
        * queueBind(String queue, String exchange, String routingKey)
        * 参数：
        *   queue：队列名称
        *   exchange：交换机名称
        *   routingKey：路由建，绑定规则
        *       如果交换机的类型为fanout，则设置为空
        * */

        // 8、发送消息
        String body="日志信息";
        channel.basicPublish(exchangeName, "goods.info", null, body.getBytes());

        // 9、释放资源
        channel.close();
        connection.close();
    }
}
