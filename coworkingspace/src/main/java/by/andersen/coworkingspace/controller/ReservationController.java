package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.dto.ReservationDto;
import by.andersen.coworkingspace.entity.Reservation;
import by.andersen.coworkingspace.service.ReservationService;
import by.andersen.coworkingspace.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
  private final JwtUtils jwtUtils;

  @Autowired
  public ReservationController(
      ReservationService reservationService,
      JwtUtils jwtUtils
  ) {
    this.reservationService = reservationService;
    this.jwtUtils = jwtUtils;
  }

  @GetMapping
  @Secured("ROLE_ADMIN")
  public ResponseEntity<List<Reservation>> getReservations() {
    List<Reservation> reservations = reservationService.getReservations();
    return ResponseEntity.ok(reservations);
  }

  @GetMapping("/user")
  public ResponseEntity<List<Reservation>> getUserReservations(@RequestParam("userId") Long userId) {
    List<Reservation> userReservations = reservationService.getUserReservations(userId);
    return ResponseEntity.ok(userReservations);
  }

  @PostMapping("/make")
  public ResponseEntity<Reservation> makeReservation(
      HttpServletRequest request,
      @RequestBody ReservationDto reservationDto) {
    String accessToken = request.getHeader("Authorization").substring(7);
    String userName = jwtUtils.extractUsername(accessToken);
    Reservation reservation = reservationService.makeReservation(userName, reservationDto);
    return ResponseEntity.ok(reservation);
  }

  @DeleteMapping("/cancel")
  public ResponseEntity<String> cancelReservation(
      HttpServletRequest request,
      @RequestParam("reservationId") Long reservationId) {
    String accessToken = request.getHeader("Authorization").substring(7);
    String userName = jwtUtils.extractUsername(accessToken);
    reservationService.cancelReservation(userName, reservationId);
    return ResponseEntity.ok("Reservation is canceled");
  }
}
