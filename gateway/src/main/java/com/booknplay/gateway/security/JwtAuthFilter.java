package com.booknplay.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Value("${jwt.secret:change-me-please-very-long-secret-key}")
    private String secret;

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(secret.getBytes())));
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
                String roles = String.valueOf(claims.get("roles"));
                ServerHttpRequest mutated = exchange.getRequest().mutate()
                        .header("X-User-Id", claims.getSubject())
                        .header("X-Roles", roles)
                        .build();
                return chain.filter(exchange.mutate().request(mutated).build());
            } catch (Exception e) {
                log.debug("JWT parse failed: {}", e.getMessage());
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() { return -100; }
}
