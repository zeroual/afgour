package com.afgour.config;

import com.afgour.repository.ActiveSessionsRepository;
import com.afgour.repository.HandsRepository;
import com.afgour.repository.RoomChatRepository;
import com.afgour.security.AuthoritiesConstants;
import com.afgour.service.ConnectionService;
import com.afgour.service.SessionEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebsocketConfiguration extends AbstractWebSocketMessageBrokerConfigurer {

    private final Logger log = LoggerFactory.getLogger(WebsocketConfiguration.class);

    public static final String IP_ADDRESS = "IP_ADDRESS";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/queue", "/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket/tracker")
            .setHandshakeHandler(new DefaultHandshakeHandler() {
                @Override
                protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                    Principal principal = request.getPrincipal();
                    if (principal == null) {
                        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        authorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
                        principal = new AnonymousAuthenticationToken("WebsocketConfiguration", "anonymous", authorities);
                    }
                    return principal;
                }
            })
            .withSockJS()
            .setInterceptors(httpSessionHandshakeInterceptor());
        registry.addEndpoint("/websocket/chat").withSockJS();
    }

    @Bean
    public HandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new HandshakeInterceptor() {

            @Override
            public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
                if (request instanceof ServletServerHttpRequest) {
                    ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
                    attributes.put(IP_ADDRESS, servletRequest.getRemoteAddress());
                }
                return true;
            }

            @Override
            public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

            }
        };
    }

    @Bean
    @Description("Keeps connected users")
    public ActiveSessionsRepository participantRepository() {
        return new ActiveSessionsRepository();
    }

    @Bean
    public RoomChatRepository roomChatRepository() {
        return new RoomChatRepository();
    }

    @Bean
    public HandsRepository handsRepository(){
        return new HandsRepository();
    }

    @Bean
    public ConnectionService connectionService(ActiveSessionsRepository activeSessionsRepository,
                                               RoomChatRepository roomChatRepository,
                                               SimpMessageSendingOperations simpMessageSendingOperations,
                                               HandsRepository handsRepository) {
        return new ConnectionService(roomChatRepository, simpMessageSendingOperations, handsRepository);
    }

    /*
 * @Bean
 *
 * @Description("Application event multicaster to process events asynchonously"
 * ) public ApplicationEventMulticaster applicationEventMulticaster() {
 * SimpleApplicationEventMulticaster multicaster = new
 * SimpleApplicationEventMulticaster();
 * multicaster.setTaskExecutor(Executors.newFixedThreadPool(10)); return
 * multicaster; }
 */
    @Bean
    @Description("Tracks user presence (join / leave) and broacasts it to all connected users")
    public SessionEventListener presenceEventListener(ActiveSessionsRepository activeSessionsRepository,
                                                      ConnectionService connectionService) {
        SessionEventListener presence = new SessionEventListener(activeSessionsRepository,connectionService);
        return presence;
    }
}
