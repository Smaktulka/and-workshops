package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.dto.ReservationDto;
import by.andersen.coworkingspace.entity.Reservation;
import by.andersen.coworkingspace.service.ReservationService;
import by.andersen.coworkingspace.service.WorkspaceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation")
public class ReservationController {
  private final ReservationService reservationService;
  private final WorkspaceService workspaceService;

  @Autowired
  public ReservationController(
      ReservationService reservationService,
      WorkspaceService workspaceService
  ) {
    this.reservationService = reservationService;
    this.workspaceService = workspaceService;
  }

  @GetMapping
  public ResponseEntity<List<Reservation>> getReservations() {
    List<Reservation> reservations = reservationService.getReservations();
    return ResponseEntity.ok(reservations);
  }

  @GetMapping
  public ResponseEntity<List<Reservation>> getUserReservations(@RequestParam("userId") Long userId) {
    List<Reservation> userReservations = reservationService.getUserReservations(userId);
    return ResponseEntity.ok(userReservations);
  }

  @PostMapping("make")
  public ResponseEntity<Reservation> makeReservation(
      @RequestParam("userId") Long userId,
      @RequestBody ReservationDto reservationDto) {
    Reservation reservation = reservationService.makeReservation(userId, reservationDto);
    return ResponseEntity.ok(reservation);
  }

  @DeleteMapping("cancel")
  public ResponseEntity<String> cancelReservation(
      @RequestParam("userId") Long userId,
      @RequestParam("reservationId") Long reservationId) {
    reservationService.cancelReservation(userId, reservationId);
    return ResponseEntity.ok("Reservation is canceled");
  }
}
