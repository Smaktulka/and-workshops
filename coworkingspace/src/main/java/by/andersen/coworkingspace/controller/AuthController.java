package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.dto.LoginDto;
import by.andersen.coworkingspace.dto.RegisterDto;
import by.andersen.coworkingspace.dto.TokensDto;
import by.andersen.coworkingspace.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/register")
  public ResponseEntity<TokensDto> register(@RequestBody RegisterDto registerDto) {
    TokensDto tokensDto = authService.register(registerDto);
    return ResponseEntity.ok(tokensDto);
  }

  @PostMapping("/login")
  public ResponseEntity<TokensDto> login(@RequestBody LoginDto loginDto) {
    TokensDto tokensDto = authService.login(loginDto);
    return ResponseEntity.ok(tokensDto);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<TokensDto> refreshToken(
      HttpServletRequest request,
      @RequestParam(name = "refresh_token") String refreshToken
  ) {
    String accessToken = request.getHeader("Authorization").substring(7);
    TokensDto tokensDto = authService.refreshToken(accessToken, refreshToken);
    return ResponseEntity.ok(tokensDto);
  }
}
