package com.hiku.userService.messaging;

import com.rabbitmq.client.Channel;
import org.json.JSONObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FollowEventPublisher {
    
    public static void publishFollowEvent(FollowEvent event) {
        try {
            Channel channel = RabbitMQConfig.getChannel();
            if (channel != null && channel.isOpen()) {
                String routingKey = "follow." + event.getAction().toLowerCase();
                String message = new JSONObject()
                    .put("followerId", event.getFollowerId())
                    .put("followedId", event.getFollowedId())
                    .put("action", event.getAction())
                    .put("timestamp", event.getTimestamp())
                    .toString();
                
                channel.basicPublish("follow.exchange", routingKey, null, message.getBytes(StandardCharsets.UTF_8));
                System.out.println("Published event: " + routingKey + " - " + message);
            }
        } catch (IOException e) {
            System.err.println("Failed to publish follow event: " + e.getMessage());
        }
    }
}