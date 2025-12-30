package com.hiku.userService.messaging;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RabbitMQConfig {
    private static Connection connection;
    private static Channel channel;

    static {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(System.getenv().getOrDefault("RABBITMQ_HOST", "rabbitmq.platform.svc.cluster.local"));
            factory.setPort(Integer.parseInt(System.getenv().getOrDefault("RABBITMQ_PORT", "5672")));
            factory.setUsername(System.getenv().getOrDefault("RABBITMQ_USER", "hikuuser"));
            factory.setPassword(System.getenv().getOrDefault("RABBITMQ_PASSWORD", "hikupassword"));
            
            connection = factory.newConnection();
            channel = connection.createChannel();
            
            // Declare exchange and queue
            channel.exchangeDeclare("follow.exchange", "topic", true);
            channel.queueDeclare("follow.queue", true, false, false, null);
            channel.queueBind("follow.queue", "follow.exchange", "follow.*");
        } catch (IOException | TimeoutException e) {
            System.err.println("Failed to connect to RabbitMQ: " + e.getMessage());
        }
    }

    public static Channel getChannel() {
        return channel;
    }
}