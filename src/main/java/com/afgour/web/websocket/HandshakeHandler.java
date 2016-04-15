package com.afgour.web.websocket;

import com.afgour.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class HandshakeHandler {

    @Autowired
    private ConnectionService connectionService;

    @SubscribeMapping("/handshake")
    public void handleNewHandshake(Principal principal) {
        connectionService.createNewConnectionFor(principal.getName());
    }
}
