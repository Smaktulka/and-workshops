package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {
  @GetMapping
  public String showCustomerPage(Model model, HttpSession session) {
    User user = (User) session.getAttribute("user");
    model.addAttribute("user", user);
    return "customer";
  }
}
