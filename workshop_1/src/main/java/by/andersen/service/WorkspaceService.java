package by.andersen.service;

import by.andersen.dto.PeriodDto;
import by.andersen.dto.WorkspaceDto;
import by.andersen.entity.Reservation;
import by.andersen.entity.Workspace;
import by.andersen.repository.ReservationRepository;
import by.andersen.repository.WorkspaceRepository;
import by.andersen.utils.Result;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkspaceService {
  private final WorkspaceRepository workspaceRepository;
  private final ReservationRepository reservationRepository;

  @Autowired
  public WorkspaceService(
      WorkspaceRepository workspaceRepository,
      ReservationRepository reservationRepository
  ) {
    this.workspaceRepository = workspaceRepository;
    this.reservationRepository = reservationRepository;
  }

  public Result<String> addWorkspace(WorkspaceDto workspaceDto) {
    Workspace workspace = new Workspace(workspaceDto);
    workspaceRepository.save(workspace);

    return Result.ok("Workspace is added");
  }

  public Result<List<Workspace>> getWorkspaces() {
    List<Workspace> workspaces = workspaceRepository.findAll();
    return Result.ok(workspaces);
  }

  public Result<String> removeWorkspaceById(Long id) {
    Optional<Workspace> optionalWorkspace = workspaceRepository.findById(id);
    if (optionalWorkspace.isEmpty()) {
      return Result.error("Workspace not found");
    }

    workspaceRepository.delete(id);

    return Result.ok("Workspace is deleted");
  }

  public Result<List<Workspace>> getAvailableWorkspaces(PeriodDto periodDto) {
    List<Workspace> workspaces = workspaceRepository.findAll();

    List<Workspace> availableWorkspacesForPeriod = workspaces.stream()
        .filter(workspace -> {
          List<Reservation> reservationsOnPeriod = reservationRepository
              .getReservationByWorkspaceIdAndPeriod(workspace.getId(), periodDto);

          return reservationsOnPeriod.isEmpty();
        })
        .collect(Collectors.toCollection(ArrayList::new));

    return Result.ok(availableWorkspacesForPeriod);
  }
}
