package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.dto.LoginDto;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @GetMapping("/login")
  public ResponseEntity<User> login(@RequestBody LoginDto loginDto) {
    User user = authService.login(loginDto);
    return ResponseEntity.ok(user);
  }
}
