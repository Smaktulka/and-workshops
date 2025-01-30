package by.andersen.coworkingspace.service;

import by.andersen.coworkingspace.dto.PeriodDto;
import by.andersen.coworkingspace.entity.Reservation;
import by.andersen.coworkingspace.entity.Workspace;
import by.andersen.coworkingspace.repository.ReservationRepository;
import by.andersen.coworkingspace.repository.WorkspaceRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
@Service
public class WorkspaceService {
  private final WorkspaceRepository workspaceRepository;
  private final ReservationRepository reservationRepository;

  public WorkspaceService(
      WorkspaceRepository workspaceRepository,
      ReservationRepository reservationRepository
  ) {
    this.workspaceRepository = workspaceRepository;
    this.reservationRepository = reservationRepository;
  }

  public List<Workspace> getWorkspaces() {
    return workspaceRepository.findAll();
  }

  public void removeWorkspaceById(Long id) {
    Optional<Workspace> optionalWorkspace = workspaceRepository.findById(id);
    if (optionalWorkspace.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Workspace not found");
    }

    workspaceRepository.deleteById(id);
  }

  public List<Workspace> getAvailableWorkspacesForPeriod(PeriodDto periodDto) {
    List<Workspace> workspaces = workspaceRepository.findAll();

    return workspaces.stream()
        .filter(workspace -> isWorkspaceAvailableForPeriod(workspace.getId(), periodDto))
        .collect(Collectors.toCollection(ArrayList::new));
  }

  public boolean isWorkspaceAvailableForPeriod(Long workspaceId, PeriodDto periodDto) {
    List<Reservation> reservationsOnPeriod = reservationRepository
        .getReservationByWorkspaceIdAndPeriodOverlap(
            workspaceId, periodDto.getStartDate(), periodDto.getEndDate());

    return reservationsOnPeriod.isEmpty();
  }
}
