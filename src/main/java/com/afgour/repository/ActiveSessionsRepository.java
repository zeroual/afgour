package com.afgour.repository;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ActiveSessionsRepository {

    private Map<String, String> activeSessions = new ConcurrentHashMap<>();

    public void save(String sessionId, String username) {
        activeSessions.put(sessionId, username);
    }

    public String getActiveUserFrom(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public void removeActiveUserWith(String sessionId) {
        activeSessions.remove(sessionId);
    }

    public int count() {
        return activeSessions.size();
    }
}
