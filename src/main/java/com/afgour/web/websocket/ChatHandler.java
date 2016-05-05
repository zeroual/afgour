package com.afgour.web.websocket;

import com.afgour.service.ConnectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.security.Principal;

@Controller
public class ChatHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChatHandler.class);

    @Autowired
    SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private ConnectionService connectionService;

    @MessageMapping("/chat")
    public void handleMessage(Message message, Principal principal) {
        System.out.println("message Received:" + message);
        String username = principal.getName();
        String partner = connectionService.findWhoIsConnectedTo(username);
        System.out.println("message from " + username + " will be send to " + partner);
        messagingTemplate.convertAndSendToUser(partner, "/queue/messages", message);
    }

    @MessageExceptionHandler
    public void handleExceptions(Throwable t) {
        logger.error("Error handling message: " + t.getMessage());
        t.printStackTrace();
    }

    private static class Message {

        private String content;

        private Message() {
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
