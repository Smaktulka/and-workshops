package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.dto.LoginDto;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.enums.UserRole;
import by.andersen.coworkingspace.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/auth")
public class AuthController {
  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @GetMapping("/login")
  public String showLoginPage(Model model) {
    model.addAttribute("loginDto", new LoginDto());
    return "login";
  }

  @PostMapping("/login")
  public String loginUser(Model model, HttpSession session, LoginDto loginDto) {
    User user = authService.login(loginDto);
    session.setAttribute("user", user);
    switch (user.getRole()) {
      case CUSTOMER -> {
        return "redirect:/customer";
      }
      case ADMIN -> {
        return "redirect:/admin";
      }
    }

    return "login";
  }
}
