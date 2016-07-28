package com.afgour.service;

import com.afgour.repository.ActiveSessionsRepository;
import com.afgour.repository.HandshakesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Optional;

/**
 * Listener to track user presence.
 */
@Service
public class SessionEventListener {

    private final ActiveSessionsRepository activeSessionsRepository;
    private final ConnectionService connectionService;
    private final HandshakesRepository handshakesRepository;

    private Logger log = LoggerFactory.getLogger(SessionEventListener.class);

    @Autowired
    public SessionEventListener(ActiveSessionsRepository activeSessionsRepository,
                                ConnectionService connectionService,
                                HandshakesRepository handshakesRepository) {
        this.activeSessionsRepository = activeSessionsRepository;
        this.connectionService = connectionService;
        this.handshakesRepository = handshakesRepository;
    }

    @EventListener
    private void handleSessionConnected(SessionConnectedEvent event) {
        SimpMessageHeaderAccessor headers = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = headers.getUser().getName();
        activeSessionsRepository.add(headers.getSessionId(), username);
        log.info("user connected : " + username);
    }

    @EventListener
    private void handleSessionDisconnect(SessionDisconnectEvent event) {

        activeSessionsRepository.remove(event.getSessionId());
        Optional.ofNullable(activeSessionsRepository.getActiveUserFrom(event.getSessionId()))
            .ifPresent(username -> {
                log.info("user disconnected " + username);
                connectionService.removeIfExistConnectionFor(username);
                handshakesRepository.removeHand(username);
            });

    }
}
