package com.afgour.service;

import com.afgour.domain.SocialUserConnection;
import com.afgour.repository.ConnectionsRepository;
import com.afgour.repository.HandsRepository;
import com.afgour.repository.SocialUserConnectionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class ConnectionService {
    private final ConnectionsRepository connectionsRepository;
    private final SimpMessageSendingOperations messagingTemplate;
    private final HandsRepository handsRepository;
    private final Logger log = LoggerFactory.getLogger(SocialService.class);

    @Autowired
    private SocialUserConnectionRepository socialUserConnectionRepository;

    @Autowired
    public ConnectionService(ConnectionsRepository connectionsRepository,
                             SimpMessageSendingOperations messagingTemplate,
                             HandsRepository handsRepository) {
        this.connectionsRepository = connectionsRepository;
        this.messagingTemplate = messagingTemplate;
        this.handsRepository = handsRepository;
    }

    public void establishNewConnectionFor(String username) {

        if (handsRepository.isEmpty()) {
            handsRepository.addHand(username);
        } else {
            Optional<String> optionalPartner = handsRepository.retrieveRandomlyOneHand(username);
            if (!optionalPartner.isPresent()) {
                return;
            }
            String randomlyUser = optionalPartner.get();
            removeIfExistConnectionFor(username);
            addNewConnection(username, randomlyUser);
        }

    }

    private void addNewConnection(String username, String randomlyUser) {
        connectionsRepository.addConnection(username, randomlyUser);
        System.out.println("new handshake " + username + " with" + randomlyUser);
        messagingTemplate.convertAndSendToUser(username, "/queue/handshake", "STARTED");
        messagingTemplate.convertAndSendToUser(randomlyUser, "/queue/handshake", "STARTED");
        log.info("connection established with " + username + " and " + randomlyUser);
    }

    private boolean userAlreadyConnected(String username) {
        return connectionsRepository.isAlreadyConnected(username);
    }

    public String findWhoIsConnectedTo(String username) {
        return connectionsRepository.findWhoIsConnectedTo(username).orElse(null);
    }

    public void removeIfExistConnectionFor(String username) {
        if (userAlreadyConnected(username)) {
            String partner = findWhoIsConnectedTo(username);
            log.info("remove connection between " + username + " and " + partner);
            messagingTemplate.convertAndSendToUser(partner, "/queue/handshake", "ENDED");
            connectionsRepository.removeConnection(username);
        }
    }

    public void askPartnerToShowIdentity(String username) {
        Optional<ConnectionsRepository.Connection> OptionalConnection = connectionsRepository.getConnectionFor(username);
        ConnectionsRepository.Connection connection = OptionalConnection.orElseThrow(() -> new IllegalStateException("askPartnerToShowIdentity: user " + username + "don't have partner"));
        connection.showIdentity(username);
        String partner = findWhoIsConnectedTo(username);
        messagingTemplate.convertAndSendToUser(partner, "/queue/identityRequest", "IDENTITY_REQUEST");
    }

    public void acceptToShowIdentity(String username) {
        Optional<ConnectionsRepository.Connection> OptionalConnection = connectionsRepository.getConnectionFor(username);
        ConnectionsRepository.Connection connection = OptionalConnection.orElseThrow(() -> new IllegalStateException("askPartnerToShowIdentity: user " + username + "don't have partner"));

        String partner = findWhoIsConnectedTo(username);
        if (connection.isIdentityRequestAlreadySentFrom(partner)) {
            messagingTemplate.convertAndSendToUser(partner, "/queue/identityResolved", getIdentityOf(username));
            messagingTemplate.convertAndSendToUser(username, "/queue/identityResolved", getIdentityOf(partner));
        }

    }

    private Identity getIdentityOf(String username) {
        Optional<SocialUserConnection> socialUserOptional = socialUserConnectionRepository.findOneByUserId(username);
        SocialUserConnection socialUser = socialUserOptional.
                orElseThrow(() -> new IllegalStateException("This user dos'nt have a social account:" + username));

        return new Identity(socialUser.getDisplayName(), socialUser.getImageURL(), socialUser.getProfileURL());
    }

    private class Identity {

        private String name;
        private String image;
        private String url;

        public Identity(String name, String image, String url) {
            this.name = name;
            this.image = image;
            this.url = url;
        }

        public String getName() {
            return name;
        }

        public String getImage() {
            return image;
        }

        public String getUrl() {
            return url;
        }
    }
}
