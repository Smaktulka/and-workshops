package by.andersen.coworkingspace.controller;

import by.andersen.coworkingspace.dto.PeriodDto;
import by.andersen.coworkingspace.dto.ReservationDto;
import by.andersen.coworkingspace.entity.Reservation;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.enums.UserRole;
import by.andersen.coworkingspace.service.ReservationService;
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
  public String getReservations(Model model, HttpSession session) {
    User user = (User) session.getAttribute("user");

    if (!user.getRole().equals(UserRole.ADMIN)) {
      return "redirect:/customer";
    }

    List<Reservation> reservations = reservationService.getReservations();
    model.addAttribute("reservations", reservations);
    return "reservations";
  }

  @GetMapping("/select-period")
  public String showSelectPeriodPage(Model model) {
    model.addAttribute("periodDto", new PeriodDto());
    return "select-period";
  }

  @PostMapping("/available-workspaces")
  public String showAvailableWorkspaces(Model model, PeriodDto periodDto) {
    model.addAttribute("availableWorkspaces", workspaceService.getAvailableWorkspacesForPeriod(periodDto));
    model.addAttribute("periodDto", periodDto);
    model.addAttribute("reservationDto", new ReservationDto());
    return "make-reservation";
  }

  @PostMapping("/make")
  public String makeReservation(HttpSession session, ReservationDto reservationDto) {
    User user = (User) session.getAttribute("user");
    reservationService.makeReservation(user.getId(), reservationDto);
    return "redirect:/reservation/show";
  }

  @GetMapping("/show")
  public String showReservations(Model model, HttpSession session) {
    User user = (User) session.getAttribute("user");
    model.addAttribute("reservations", reservationService.getUserReservations(user.getId()));
    return "show-reservations";
  }

  @GetMapping("/cancel")
  public String cancelReservation(Model model, HttpSession session) {
    User user = (User) session.getAttribute("user");
    model.addAttribute("reservations", reservationService.getUserReservations(user.getId()));
    return "cancel-reservation";
  }

  @PostMapping("/cancel")
  public String cancelReservationPost(Long userId, Long reservationId) {
    reservationService.cancelReservation(userId, reservationId);
    return "redirect:/reservation/show";
  }
}
