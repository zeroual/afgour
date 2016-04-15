package com.afgour.service;

import com.afgour.repository.ActiveSessionsRepository;
import com.afgour.repository.HandsRepository;
import com.afgour.repository.RoomChatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;


@Service
public class ConnectionService {
    private final ActiveSessionsRepository activeSessionsRepository;
    private final RoomChatRepository roomChatRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final HandsRepository handsRepository;

    @Autowired
    public ConnectionService(ActiveSessionsRepository activeSessionsRepository, RoomChatRepository roomChatRepository,
                             SimpMessageSendingOperations messagingTemplate, HandsRepository handsRepository) {
        this.activeSessionsRepository = activeSessionsRepository;
        this.roomChatRepository = roomChatRepository;
        this.messagingTemplate = messagingTemplate;
        this.handsRepository = handsRepository;
    }

    public void createNewConnectionFor(String username) {


        if (handsRepository.isEmpty()) {
            handsRepository.addHand(username);
        } else {
            String randomlyUser = findRandomlyNewUser();
            if (userAlreadyConnected(username)) {
                String partner = findWhoIsConnectedTo(username);
                messagingTemplate.convertAndSendToUser(partner, "/queue/handshake", "ENDED");
                roomChatRepository.removeConnection(username);
            }
            roomChatRepository.addConnection(username, randomlyUser);
            System.out.println("new handshake " + username + " with" + randomlyUser);
            messagingTemplate.convertAndSendToUser(username, "/queue/handshake", "STARTED");
            messagingTemplate.convertAndSendToUser(randomlyUser, "/queue/handshake", "STARTED");
        }

    }

    private boolean userAlreadyConnected(String username) {
        return roomChatRepository.isAlreadyConnected(username);
    }

    private String findRandomlyNewUser() {
        return handsRepository.retrieveRandomlyOneHand();
    }

    public String findWhoIsConnectedTo(String username) {
        return roomChatRepository.findWhoIsConnectedTo(username).orElse(null);
    }

}
