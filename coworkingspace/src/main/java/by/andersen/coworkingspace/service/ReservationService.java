package by.andersen.coworkingspace.service;

import by.andersen.coworkingspace.dto.PeriodDto;
import by.andersen.coworkingspace.dto.ReservationDto;
import by.andersen.coworkingspace.entity.Reservation;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.entity.Workspace;
import by.andersen.coworkingspace.repository.ReservationRepository;
import by.andersen.coworkingspace.repository.UserRepository;
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

  private final UserRepository userRepository;

  @Autowired
  public ReservationService(
      ReservationRepository reservationRepository,
      WorkspaceRepository workspaceRepository,
      UserRepository userRepository
  ) {
    this.reservationRepository = reservationRepository;
    this.workspaceRepository = workspaceRepository;
    this.userRepository = userRepository;
  }

  public List<Reservation> getReservations() {
    return reservationRepository.findAll();
  }

  public List<Reservation> getUserReservations(Long userId) {
    return reservationRepository.findByOwnerId(userId);
  }

  public void cancelReservation(String userName, Long reservationId) {
    Reservation reservation = reservationRepository.findById(reservationId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found"));

    User user = userRepository.findByName(userName).get();

    if (!reservation.getOwner().getId().equals(user.getId())) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot cancel someone's else reservation");
    }

    reservationRepository.deleteById(reservationId);
  }

  public Reservation makeReservation(String userName, ReservationDto reservationDto) {
    Optional<Workspace> optionalWorkspace = workspaceRepository.findById(reservationDto.getWorkspaceId());
    if (optionalWorkspace.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,  "Workspace not found");
    }

    Workspace workspace = optionalWorkspace.get();
    PeriodDto periodDto = new PeriodDto(reservationDto.getStartTime(), reservationDto.getEndTime());
    if (!isWorkspaceAvailable(workspace.getId(), periodDto)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot create reservation on the specified date");
    }

    User user = userRepository.findByName(userName).get();

    Reservation reservation = Reservation.builder()
        .owner(user)
        .workspace(workspace)
        .startTime(reservationDto.getStartTime())
        .endTime(reservationDto.getEndTime())
        .build();

    reservationRepository.save(reservation);

    return reservation;
  }

  public boolean isWorkspaceAvailable(Long workspaceId, PeriodDto periodDto) {
    List<Reservation> reservationsOnPeriod = reservationRepository
        .getReservationByWorkspaceIdAndPeriodOverlap(workspaceId, periodDto.getStartTime(), periodDto.getEndTime());
    return reservationsOnPeriod.isEmpty();
  }
}
