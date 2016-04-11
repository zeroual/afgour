package com.afgour.web.websocket;

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

    @MessageMapping("/chat")
    public Message handleMessage(Message message, Principal principal) {
        System.out.println("message Received:" + message);
        System.out.println("message from "+ principal.getName()+" will be send to "+ message .getTo());
        messagingTemplate.convertAndSendToUser(message.getTo(), "/queue/notifications", message);
        return message;
    }

    @MessageExceptionHandler
    public void handleExceptions(Throwable t) {
        logger.error("Error handling message: " + t.getMessage());
        t.printStackTrace();
    }
    private static class Message {

        private String message;
        private String to;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public Message() {

        }

        @Override
        public String toString() {
            return "Message{" +
                "message='" + message + '\'' +
                ", to='" + to + '\'' +
                '}';
        }
    }
}
