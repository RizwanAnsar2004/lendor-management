package com.synergyresources.gcp.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

  private static final List<String> OPEN_PATHS = List.of(
      "/auth/", "/health", "/ready", "/v1/lenders/",
      "/swagger-ui", "/v3/api-docs", "/webjars/",
      "/auth-docs/", "/borrower-docs/", "/lender-docs/",
      "/passport-docs/", "/audit-docs/"
  );

  @Value("${gcp.jwt.secret:change-me-dev-secret-at-least-32-bytes-long!}")
  private String jwtSecret;

  @Value("${gcp.security.enforce-jwt:false}")
  private boolean enforceJwt;

  @Override
  public int getOrder() { return -100; }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    if (!enforceJwt) {
      return chain.filter(exchange);
    }

    String path = exchange.getRequest().getURI().getPath();
    if (OPEN_PATHS.stream().anyMatch(path::startsWith)) {
      return chain.filter(exchange);
    }

    String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return unauthorized(exchange, "Authorization header missing or malformed");
    }

    String token = authHeader.substring(7);
    Claims claims;
    try {
      SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
      claims = Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (JwtException e) {
      return unauthorized(exchange, "Invalid or expired token");
    }

    String userId = claims.getSubject();
    String email = claims.get("email", String.class);
    String role = claims.get("role", String.class);

    ServerHttpRequest mutated = exchange.getRequest().mutate()
        .header("X-User-Id", userId)
        .header("X-User-Email", email != null ? email : "")
        .header("X-User-Role", role != null ? role : "")
        .build();

    return chain.filter(exchange.mutate().request(mutated).build());
  }

  private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
    exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
    byte[] body = ("{\"error\":\"" + message + "\",\"status\":401}").getBytes(StandardCharsets.UTF_8);
    var buf = exchange.getResponse().bufferFactory().wrap(body);
    return exchange.getResponse().writeWith(Mono.just(buf));
  }
}
