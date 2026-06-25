package com.synergyresources.gcp.auth.service;

import com.synergyresources.gcp.auth.config.AuthProperties;
import com.synergyresources.gcp.auth.model.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

  private final AuthProperties props;

  public JwtService(AuthProperties props) { this.props = props; }

  private SecretKey key() {
    return Keys.hmacShaKeyFor(props.getJwt().getSecret().getBytes(StandardCharsets.UTF_8));
  }

  public String issue(AppUser user) {
    var now = Instant.now();
    return Jwts.builder()
        .subject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("firstName", user.getFirstName())
        .claim("lastName", user.getLastName())
        .claim("role", user.getRole())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(props.getJwt().getExpirySeconds())))
        .signWith(key())
        .compact();
  }

  public Claims parse(String token) {
    try {
      return Jwts.parser()
          .verifyWith(key())
          .build()
          .parseSignedClaims(token)
          .getPayload();
    } catch (JwtException e) {
      throw new AuthException("Invalid or expired token", 401);
    }
  }

  public UUID extractUserId(String token) {
    return UUID.fromString(parse(token).getSubject());
  }

  public long expirySeconds() { return props.getJwt().getExpirySeconds(); }
}
