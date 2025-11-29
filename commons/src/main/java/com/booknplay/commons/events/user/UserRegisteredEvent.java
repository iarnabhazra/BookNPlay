package com.booknplay.commons.events.user;

import com.booknplay.commons.events.BaseEvent;

public class UserRegisteredEvent extends BaseEvent {

    private final String userId;
    private final String email;
    private final String role;

    public UserRegisteredEvent(String userId, String email, String role) {
        this.userId = userId;
        this.email = email;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    @Override
    public String type() { return "user.registered"; }
}
