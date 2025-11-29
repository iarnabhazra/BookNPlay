package com.booknplay.notification.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.support.ChannelInterceptor;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import java.security.Key;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Value("${jwt.secret:change-me-please-very-long-secret-key}")
    private String secret;

    private Key key() { return Keys.hmacShaKeyFor(Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(secret.getBytes()))); }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            private final io.micrometer.core.instrument.Counter authFailures = io.micrometer.core.instrument.Counter
                    .builder("ws.auth.failures").description("Failed websocket auth attempts").register(meterRegistry());

            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                Object token = message.getHeaders().get("simpSessionAttributes.token");
                if (token instanceof String t) {
                    try { Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(t); }
                    catch (Exception e) { authFailures.increment(); return null; }
                }
                return message;
            }
        });
    }

    // Lazy holder to avoid early initialization issues
    private MeterRegistry meterRegistry() {
        return org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext()
                .getBean(MeterRegistry.class);
    }
}
