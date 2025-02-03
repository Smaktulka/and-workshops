package by.andersen.coworkingspace.service;

import by.andersen.coworkingspace.dto.PeriodDto;
import by.andersen.coworkingspace.dto.ReservationDto;
import by.andersen.coworkingspace.entity.Reservation;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.entity.Workspace;
import by.andersen.coworkingspace.repository.ReservationRepository;
import by.andersen.coworkingspace.repository.WorkspaceRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservationService {
  private final ReservationRepository reservationRepository;
  private final WorkspaceRepository workspaceRepository;

  @Autowired
  public ReservationService(
      ReservationRepository reservationRepository,
      WorkspaceRepository workspaceRepository
  ) {
    this.reservationRepository = reservationRepository;
    this.workspaceRepository = workspaceRepository;
  }

  public List<Reservation> getReservations() {
    return reservationRepository.findAll();
  }

  public List<Reservation> getUserReservations(Long userId) {
    return reservationRepository.findByOwnerId(userId);
  }

  public void cancelReservation(Long userId, Long reservationId) {
    Reservation reservation = reservationRepository.getReferenceById(reservationId);

    if (reservation.getOwner().getId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot cancel someone's else reservation");
    }

    reservationRepository.deleteById(reservationId);
  }

  public Reservation makeReservation(Long userId, ReservationDto reservationDto) {
    Optional<Workspace> optionalWorkspace = workspaceRepository.findById(reservationDto.getWorkspaceId());
    if (optionalWorkspace.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Workspace not found");
    }

    Workspace workspace = optionalWorkspace.get();
    if (!isWorkspaceAvailable(workspace.getId(), reservationDto.getPeriod())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create reservation on specified date");
    }

    Reservation reservation = Reservation.builder()
        .id(0L)
        .owner(new User(userId))
        .workspace(workspace)
        .startTime(reservationDto.getStartTime())
        .endTime(reservationDto.getEndTime())
        .build();

    reservationRepository.save(reservation);

    return reservation;
  }

  public boolean isWorkspaceAvailable(Long workspaceId, PeriodDto periodDto) {
    List<Reservation> reservationsOnPeriod = reservationRepository
        .getReservationByWorkspaceIdAndPeriodOverlap(workspaceId, periodDto.getStartDate(), periodDto.getEndDate());
    return reservationsOnPeriod.isEmpty();
  }
}
