package com.afgour.service;

import com.afgour.repository.HandsRepository;
import com.afgour.repository.RoomChatRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ConnectionService {
    private final RoomChatRepository roomChatRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final HandsRepository handsRepository;
    private final Logger log = LoggerFactory.getLogger(SocialService.class);
    @Autowired
    public ConnectionService(RoomChatRepository roomChatRepository,
                             SimpMessageSendingOperations messagingTemplate,
                             HandsRepository handsRepository) {
        this.roomChatRepository = roomChatRepository;
        this.messagingTemplate = messagingTemplate;
        this.handsRepository = handsRepository;
    }

    public void establishNewConnectionFor(String username) {

        if (handsRepository.isEmpty()) {
            handsRepository.addHand(username);
        } else {
            Optional<String> optionalPartner = handsRepository.retrieveRandomlyOneHand(username);
            if(!optionalPartner.isPresent()){
                return ;
            }
            String randomlyUser = optionalPartner.get();
            removeIfExistConnectionFor(username);
            addNewConnection(username, randomlyUser);
        }

    }

    private void addNewConnection(String username, String randomlyUser) {
        roomChatRepository.addConnection(username, randomlyUser);
        System.out.println("new handshake " + username + " with" + randomlyUser);
        messagingTemplate.convertAndSendToUser(username, "/queue/handshake", "STARTED");
        messagingTemplate.convertAndSendToUser(randomlyUser, "/queue/handshake", "STARTED");
        log.info("connection established with " + username + " and " + randomlyUser);
    }

    private boolean userAlreadyConnected(String username) {
        return roomChatRepository.isAlreadyConnected(username);
    }

    public String findWhoIsConnectedTo(String username) {
        return roomChatRepository.findWhoIsConnectedTo(username).orElse(null);
    }
    public void removeIfExistConnectionFor(String username){
        if (userAlreadyConnected(username)) {
            String partner = findWhoIsConnectedTo(username);
            log.info("remove connection between "+username+" and "+partner);
            messagingTemplate.convertAndSendToUser(partner, "/queue/handshake", "ENDED");
            roomChatRepository.removeConnection(username);
        }
    }

}
