package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
  @GetMapping
  public String showAdminPage(Model model, HttpSession session) {
    User user = (User) session.getAttribute("user");
    model.addAttribute("user", user);
    return "admin";
  }
}
