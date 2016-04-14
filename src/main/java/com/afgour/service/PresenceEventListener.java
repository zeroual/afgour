package com.afgour.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.HashMap;

/**
 * Listener to track user presence.
 * Sends notifications to the login destination when a connected event is received
 * and notifications to the logout destination when a disconnect event is received
 */
@Service
public class PresenceEventListener {

    private HashMap<String, String> participants = new HashMap<>();

    private final Logger log = LoggerFactory.getLogger(PresenceEventListener.class);

    public PresenceEventListener() {
    }

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = headers.getUser().getName();
        // We store the session as we need to be idempotent in the disconnect event processing
        log.info("user connected " + username);
        participants.put(headers.getSessionId(), username);
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {

        log.info("user disconnected " + participants.get(event.getSessionId()));
        participants.remove(event.getSessionId());
    }
}
