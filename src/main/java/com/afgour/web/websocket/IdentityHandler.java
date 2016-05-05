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
@RequestMapping(value = "/identity/request")
public class IdentityHandler {

    private final Logger log = LoggerFactory.getLogger(SocialService.class);

    @Autowired
    private ConnectionService connectionService;

    @RequestMapping(value = "/ask",method = RequestMethod.GET)
    public void askToShowIdentity(Principal principal) {
        log.info("the user " + principal.getName() + " ask partner to show his identity");
        connectionService.askPartnerToShowIdentity(principal.getName());
    }

    @RequestMapping(value = "/accept",method = RequestMethod.POST)
    public void acceptToShowIdentity(Principal principal) {
        log.info("the user " + principal.getName() + " accept to show his identity");
        connectionService.acceptToShowIdentity(principal.getName());
    }
}
