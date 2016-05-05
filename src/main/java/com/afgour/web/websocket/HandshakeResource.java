package com.afgour.web.websocket;

import com.afgour.service.ConnectionService;
import com.afgour.service.SocialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class HandshakeResource {

    private final Logger log = LoggerFactory.getLogger(SocialService.class);

    @Autowired
    private ConnectionService connectionService;

    @RequestMapping(value = "/chat/handshake", method = RequestMethod.GET)
    public void getHandshake(Principal principal) {
        log.info("the user " + principal.getName() + " ask for a handshake");
        connectionService.establishNewConnectionFor(principal.getName());
    }
}
