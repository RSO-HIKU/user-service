package com.hiku.userService.messaging;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FollowEvent implements Serializable {
    private Long followerId;
    private Long followedId;
    private String action; // "CREATED" or "REMOVED"
    private LocalDateTime timestamp;

    public FollowEvent(Long followerId, Long followedId, String action) {
        this.followerId = followerId;
        this.followedId = followedId;
        this.action = action;
        this.timestamp = LocalDateTime.now();
    }

    public Long getFollowerId() {
        return followerId;
    }

    public Long getFollowedId() {
        return followedId;
    }

    public String getAction() {
        return action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}