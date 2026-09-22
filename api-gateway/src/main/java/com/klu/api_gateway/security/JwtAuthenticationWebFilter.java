package com.klu.api_gateway.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtService jwtService;

    public JwtAuthenticationWebFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            WebFilterChain chain) {

        String path = exchange.getRequest()
                .getURI()
                .getPath();

        /*
         * Authentication endpoints are public.
         */
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        /*
         * JWT is required for protected endpoints.
         */
        if (authorizationHeader == null ||
                !authorizationHeader.startsWith("Bearer ")) {

            return unauthorized(exchange);
        }

        String token = authorizationHeader.substring(7);

        /*
         * Validate JWT signature and expiration.
         */
        if (!jwtService.validateToken(token)) {
            return unauthorized(exchange);
        }

        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token);

        if (username == null) {
            return unauthorized(exchange);
        }

        String authority = role != null
                ? "ROLE_" + role
                : "ROLE_USER";

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority(authority)));

        SecurityContext securityContext = new SecurityContextImpl(authentication);

        return chain.filter(exchange)
                .contextWrite(
                        ReactiveSecurityContextHolder
                                .withSecurityContext(
                                        Mono.just(securityContext)));
    }

    private boolean isPublicEndpoint(String path) {

        return path.equals("/api/auth/register")
                || path.equals("/api/auth/login")
                || path.equals("/api/auth/validate")
                || path.equals("/api/auth/health")
                || path.startsWith("/actuator/")
                || path.equals("/error")
                || path.startsWith("/auth-service/");
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }
}