package com.afgour.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class ActiveSessionsRepository {

    private Logger log = LoggerFactory.getLogger(ActiveSessionsRepository.class);

    private Map<String, String> activeSessions = new ConcurrentHashMap<>();

    public String getActiveUserFrom(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public int count() {
        return activeSessions.size();
    }

    public void add(String sessionId, String username) {
        if (activeSessions.containsValue(username)) {
            log.error("Hum, add new session, a user have already session {} , ", username);
        }
        activeSessions.put(sessionId, username);
    }

    public void remove(String sessionId) {
        if (!activeSessions.containsKey(sessionId)) {
            log.error("Hum,remove session ; this user has no session");
        }
        activeSessions.remove(sessionId);
    }
}
