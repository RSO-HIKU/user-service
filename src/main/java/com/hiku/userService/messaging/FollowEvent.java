package com.hiku.userService.messaging;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FollowEvent implements Serializable {
    private String followerId;
    private String followedId;
    private String action; // "CREATED" or "REMOVED"
    private LocalDateTime timestamp;

    public FollowEvent(String followerId, String followedId, String action) {
        this.followerId = followerId;
        this.followedId = followedId;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    public String getFollowerId() {
        return followerId;
    }

    public String getFollowedId() {
        return followedId;
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}