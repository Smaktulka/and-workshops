package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.dto.PeriodDto;
import by.andersen.coworkingspace.entity.Workspace;
import by.andersen.coworkingspace.service.WorkspaceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/workspace")
public class WorkspaceController {
  private final WorkspaceService workspaceService;

  @Autowired
  public WorkspaceController(WorkspaceService workspaceService) {
    this.workspaceService = workspaceService;
  }

  @GetMapping
  public ResponseEntity<List<Workspace>> getWorkspaces() {
    List<Workspace> workspaces = workspaceService.getWorkspaces();
    return ResponseEntity.ok(workspaces);
  }

  @DeleteMapping("remove")
  public ResponseEntity<String> removeWorkspace(@RequestParam("workspaceId") Long workspaceId) {
    workspaceService.removeWorkspaceById(workspaceId);
    return ResponseEntity.ok("Workspace is removed");
  }

  @GetMapping("available")
  public ResponseEntity<List<Workspace>> getAvailableWorkspaces(@RequestBody PeriodDto periodDto) {
    List<Workspace> availableWorkspaces = workspaceService.getAvailableWorkspacesForPeriod(periodDto);
    return ResponseEntity.ok(availableWorkspaces);
  }
}
