package com.booknplay.gateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class PropagateHeadersFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        ServerHttpRequest.Builder mut = req.mutate();
        if (req.getHeaders().getFirst("X-User-Id") == null) {
            mut.header("X-User-Id", "anonymous");
        }
        return chain.filter(exchange.mutate().request(mut.build()).build());
    }

    @Override
    public int getOrder() { return 0; }
}
