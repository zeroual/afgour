package com.afgour.repository;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class RoomChatRepository {

    private Set<Connection> connectionList = Collections.synchronizedSet(new HashSet<>());

    public RoomChatRepository() {
    }

    public void addConnection(String username1, String username2) {
        connectionList.add(new Connection(username1, username2));
    }

    public void removeConnection(String username) {
        connectionList.stream().filter(connection -> connection.isBoundTo(username)).findFirst().ifPresent(
            connection -> connectionList.remove(connection)
        );
    }

    public boolean isAlreadyConnected(String username) {
        return connectionList.stream().filter(connection -> connection.isBoundTo(username)).findFirst().isPresent();
    }

    public Optional<String> findWhoIsConnectedTo(String username) {
        return connectionList.stream().filter(connection -> connection.isBoundTo(username))
                                      .map(connection -> connection.getPartnerOf(username)).findFirst();
    }

    public static class Connection {
        private final String user1;
        private final String user2;

        public Connection(String user1, String user2) {
            this.user1 = user1;
            this.user2 = user2;
        }

        public boolean isBoundTo(String username) {
            return username.equals(user1) || username.equals(user2);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj instanceof Connection) {
                Connection connection = (Connection) obj;
                return (connection.isBoundTo(user1) && connection.isBoundTo(user2));
            }
            return false;
        }

        public String getPartnerOf(String username) {
            if (user1.equals(username))
                return user2;
            else
                return user1;
        }
    }
}
