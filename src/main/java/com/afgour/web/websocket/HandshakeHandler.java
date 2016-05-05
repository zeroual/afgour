package com.afgour.web.websocket;

import com.afgour.service.ConnectionService;
import com.afgour.service.SocialService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class HandshakeHandler {

    private final Logger log = LoggerFactory.getLogger(SocialService.class);

    @Autowired
    private ConnectionService connectionService;

    @SubscribeMapping("/askToShowIdentity")
    public void askToShowIdentity(Principal principal) {
        log.info("the user " +principal.getName()+" ask partner to show his identity");
        connectionService.askPartnerToShowIdentity(principal.getName());
    }

    @SubscribeMapping("/acceptToShowIdentity")
    public void acceptToShowIdentity(Principal principal) {
        log.info("the user " +principal.getName()+" accept to show his identity");
        connectionService.acceptToShowIdentity(principal.getName());
    }
}
