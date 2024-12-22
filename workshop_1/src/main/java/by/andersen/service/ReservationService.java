package by.andersen.service;

import by.andersen.dto.ReservationDto;
import by.andersen.dto.PeriodDto;
import by.andersen.entity.Reservation;
import by.andersen.entity.Workspace;
import by.andersen.repository.ReservationRepository;
import by.andersen.repository.WorkspaceRepository;
import by.andersen.utils.Result;
import java.util.List;
import java.util.Optional;

public class ReservationService {
  private final ReservationRepository reservationRepository;
  private final WorkspaceRepository workspaceRepository;

  public ReservationService(
      ReservationRepository reservationRepository,
      WorkspaceRepository workspaceRepository
  ) {
    this.reservationRepository = reservationRepository;
    this.workspaceRepository = workspaceRepository;
  }

  public Result<List<Reservation>> getReservations() {
    List<Reservation> reservations = reservationRepository.findAll();
    return Result.ok(reservations);
  }

  public Result<List<Reservation>> getUserReservations(Long userId) {
    List<Reservation> userReservations = reservationRepository.getReservationsByOwnerId(userId);
    return Result.ok(userReservations);
  }

  public Result<String> cancelReservation(Long userId, Long reservationId) {
    Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);
    if (!optionalReservation.isPresent()) {
      return Result.error("Cannot find reservation");
    }

    if (!optionalReservation.get().getOwnerId().equals(userId)) {
      return Result.error("Cannot cancel someone's else reservation");
    }

    reservationRepository.delete(reservationId);

    return Result.ok("Reservation is cancelled");
  }

  public Result<Reservation> makeReservation(Long userId, ReservationDto reservationDto) {
    Optional<Workspace> optionalWorkspace = workspaceRepository.findById(reservationDto.getWorkspaceId());
    if (!optionalWorkspace.isPresent()) {
      return Result.error("Workspace not found");
    }

    Workspace workspace = optionalWorkspace.get();
    if (!isWorkspaceAvailable(workspace.getId(), reservationDto.getPeriod())) {
      return Result.error("Cannot create reservation on specified date");
    }

    Long lastId = reservationRepository.getLastId();
    if (lastId == null) {
      lastId = 0L;
    }

    Reservation reservation = Reservation.builder()
        .id(lastId + 1)
        .ownerId(userId)
        .workspaceId(reservationDto.getWorkspaceId())
        .startTime(reservationDto.getStartTime())
        .endTime(reservationDto.getEndTime())
        .build();

    reservationRepository.save(reservation);

    return Result.ok(reservation);
  }

  private boolean isWorkspaceAvailable(Long workspaceId, PeriodDto periodDto) {
    List<Reservation> reservationsOnPeriod = reservationRepository
        .getReservationByWorkspaceIdAndPeriod(workspaceId, periodDto);
    return reservationsOnPeriod.isEmpty();
  }
}