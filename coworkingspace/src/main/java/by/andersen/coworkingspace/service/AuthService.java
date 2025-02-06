package by.andersen.coworkingspace.service;

import by.andersen.coworkingspace.dto.LoginDto;
import by.andersen.coworkingspace.dto.RegisterDto;
import by.andersen.coworkingspace.dto.TokensDto;
import by.andersen.coworkingspace.entity.Token;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.repository.TokenRepository;
import by.andersen.coworkingspace.repository.UserRepository;
import by.andersen.coworkingspace.utils.JwtUtils;
import by.andersen.coworkingspace.utils.PasswordHashUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final JwtUtils jwtUtils;

  @Autowired
  public AuthService(
      UserRepository userRepository,
      TokenRepository tokenRepository,
      JwtUtils jwtUtils
  ) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.jwtUtils = jwtUtils;
  }

  public TokensDto register(RegisterDto registerDto) {
    User newUser = User.builder()
        .name(registerDto.getUserName())
        .role(registerDto.getRole())
        .passwordHash(PasswordHashUtils.hash(registerDto.getPassword()))
        .build();

    newUser = userRepository.save(newUser);
    TokensDto tokens = jwtUtils.generateTokens(newUser);
    saveToken(newUser, tokens.getAccessToken());

    return tokens;
  }

  @Transactional
  public TokensDto login(LoginDto loginDto) {
    User user = userRepository.findByName(loginDto.getUserName())
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "User with name " + loginDto.getUserName() + " is not found"));

    boolean isPasswordValid = PasswordHashUtils.
        verifyPassword(loginDto.getPassword(), user.getPasswordHash());

    if (!isPasswordValid) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid password");
    }

    TokensDto tokens = jwtUtils.generateTokens(user);
    deleteUserTokens(user);
    saveToken(user, tokens.getAccessToken());

    return tokens;
  }

  public TokensDto refreshToken(
      HttpServletRequest request
  ) {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot refresh bearer token without token");
    }

    String refreshToken = authHeader.substring(7);
    String userName = jwtUtils.extractUsername(refreshToken);

    if (userName == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token user name is empty");
    }

    User user = userRepository.findByName(userName)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, "User with name " + userName + " is not found"));

    if (!jwtUtils.validateToken(refreshToken, user.getName())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token is invalid");
    }

    TokensDto tokensDto = jwtUtils.generateTokens(user);
    deleteUserTokens(user);
    saveToken(user, tokensDto.getAccessToken());

    return tokensDto;
  }

  private void saveToken(User user, String jwt) {
    Token token = Token.builder()
        .token(jwt)
        .user(user)
        .build();

    tokenRepository.save(token);
  }

  @Transactional
  private void deleteUserTokens(User user) {
    tokenRepository.deleteByUser(user);
  }
}
