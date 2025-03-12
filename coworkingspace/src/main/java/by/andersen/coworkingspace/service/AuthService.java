package by.andersen.coworkingspace.service;

import by.andersen.coworkingspace.dto.LoginDto;
import by.andersen.coworkingspace.dto.RegisterDto;
import by.andersen.coworkingspace.dto.TokensDto;
import by.andersen.coworkingspace.entity.RefreshToken;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.repository.RefreshTokenRepository;
import by.andersen.coworkingspace.repository.UserRepository;
import by.andersen.coworkingspace.utils.JwtUtils;
import by.andersen.coworkingspace.utils.PasswordHashUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final JwtUtils jwtUtils;

  @Autowired
  public AuthService(
      UserRepository userRepository,
      RefreshTokenRepository refreshTokenRepository,
      JwtUtils jwtUtils
  ) {
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.jwtUtils = jwtUtils;
  }

  public TokensDto register(RegisterDto registerDto) {
    User newUser = User.builder()
        .name(registerDto.getUserName())
        .role(registerDto.getRole())
        .passwordHash(PasswordHashUtils.hash(registerDto.getPassword()))
        .build();

    if (userRepository.findByName(newUser.getName()).isPresent()) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User name is taken");
    }

    newUser = userRepository.save(newUser);
    TokensDto tokens = jwtUtils.generateTokens(newUser);
    saveRefreshToken(newUser, tokens.getRefreshToken());

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
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password");
    }

    TokensDto tokens = jwtUtils.generateTokens(user);
    refreshTokenRepository.deleteByUser(user);
    saveRefreshToken(user, tokens.getRefreshToken());

    return tokens;
  }

  @Transactional
  public TokensDto refreshToken(String accessToken, String refreshToken) {
    RefreshToken refreshTokenEntity = refreshTokenRepository.getByRefreshToken(refreshToken)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Refresh token is not found"));

    String userName = jwtUtils.extractUsername(accessToken);

    if (!jwtUtils.validateToken(refreshToken, userName)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Refresh token is invalid");
    }

    TokensDto tokensDto = jwtUtils.generateTokens(refreshTokenEntity.getUser());
    refreshTokenRepository.deleteByUser(refreshTokenEntity.getUser());
    saveRefreshToken(refreshTokenEntity.getUser(), tokensDto.getRefreshToken());

    return tokensDto;
  }

  private void saveRefreshToken(User user, String jwt) {
    RefreshToken refreshToken = RefreshToken.builder()
        .refreshToken(jwt)
        .user(user)
        .build();

    refreshTokenRepository.save(refreshToken);
  }
}