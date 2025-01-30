package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.entity.Workspace;
import by.andersen.coworkingspace.enums.UserRole;
import by.andersen.coworkingspace.service.WorkspaceService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/workspace")
public class WorkspaceController {
  private final WorkspaceService workspaceService;

  @Autowired
  public WorkspaceController(WorkspaceService workspaceService) {
    this.workspaceService = workspaceService;
  }

  @GetMapping
  public String getWorkspaces(Model model, HttpSession session) {
    User user = (User) session.getAttribute("user");

    if (!user.getRole().equals(UserRole.ADMIN)) {
      return "redirect:/customer";
    }

    List<Workspace> workspaces = workspaceService.getWorkspaces();
    model.addAttribute("workspaces", workspaces);

    return "workspaces";
  }

  @GetMapping("remove")
  public String showWorkspaceRemovePage(Model model, HttpSession session) {
    User user = (User) session.getAttribute("user");

    if (!user.getRole().equals(UserRole.ADMIN)) {
      return "redirect:/customer";
    }

    List<Workspace> workspaces = workspaceService.getWorkspaces();
    model.addAttribute("workspaces", workspaces);
    return "remove-workspace";
  }

  @PostMapping("remove")
  public String removeWorkspace(Long workspaceId) {
    workspaceService.removeWorkspaceById(workspaceId);
    return "redirect:/admin";
  }
}
