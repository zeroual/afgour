package com.afgour.repository;

import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Repository;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ActiveSessionsRepository {

    private Map<String, String> activeSessions = new ConcurrentHashMap<>();
    private SimpUserRegistry simpUserRegistry = new DefaultSimpUserRegistry();

    public String getActiveUserFrom(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public int count() {
        return simpUserRegistry.getUsers().size();
    }
}
