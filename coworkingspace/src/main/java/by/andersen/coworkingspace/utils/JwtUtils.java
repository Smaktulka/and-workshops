package by.andersen.coworkingspace.utils;

import by.andersen.coworkingspace.dto.TokensDto;
import by.andersen.coworkingspace.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class JwtUtils {
  private static final String SECRET_KEY = "secret";
  private static final int JWT_TTL_IN_MINUTES = 5;
  private static final int REFRESH_TOKEN_TTL_IN_MINUTES = 120;

  public TokensDto generateTokens(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("role", user.getRole());
    String jwt = generateToken(claims, user);
    String refreshToken = generateRefreshToken(claims, user);

    return new TokensDto(jwt, refreshToken);
  }

  public String generateToken(Map<String, Object> claims, User user) {
        return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getName())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(JWT_TTL_IN_MINUTES)))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }

  public String generateRefreshToken(Map<String, Object> claims, User user) {
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getName())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(REFRESH_TOKEN_TTL_IN_MINUTES)))
        .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
        .compact();
  }

  public boolean validateToken(String token, String username) {
    String extractedUsername = extractUsername(token);
    return (extractedUsername.equals(username) && !isTokenExpired(token));
  }

  public String extractUsername(String token) {
    return extractAllClaims(token).getSubject();
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
  }

  private boolean isTokenExpired(String token) {
    return extractAllClaims(token).getExpiration().before(new Date());
  }
}
