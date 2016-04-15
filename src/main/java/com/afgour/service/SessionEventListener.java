package com.afgour.service;

import com.afgour.repository.ActiveSessionsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

/**
 * Listener to track user presence.
 */
@Service
public class SessionEventListener {

    private final ActiveSessionsRepository activeSessionsRepository;

    private  Logger log = LoggerFactory.getLogger(SessionEventListener.class);

    @Autowired
    public SessionEventListener(ActiveSessionsRepository activeSessionsRepository) {
        this.activeSessionsRepository = activeSessionsRepository;
    }

    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = headers.getUser().getName();
        // We store the session as we need to be idempotent in the disconnect event processing
        log.info("user connected " + username);
        activeSessionsRepository.save(headers.getSessionId(), username);
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {

        Optional.ofNullable(activeSessionsRepository.getActiveUserFrom(event.getSessionId()))
            .ifPresent(username -> {
                log.info("user disconnected " + username);
                activeSessionsRepository.removeActiveUserWith(event.getSessionId());
            });

    }
}
