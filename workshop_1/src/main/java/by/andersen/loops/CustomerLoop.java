package by.andersen.loops;

import by.andersen.context.AppContext;
import by.andersen.dto.PeriodDto;
import by.andersen.dto.ReservationDto;
import by.andersen.entity.Reservation;
import by.andersen.entity.User;
import by.andersen.entity.Workspace;
import by.andersen.enums.command.CustomerLoopCommand;
import by.andersen.repository.ReservationRepository;
import by.andersen.repository.WorkspaceRepository;
import by.andersen.service.ReservationService;
import by.andersen.service.WorkspaceService;
import by.andersen.utils.CommandPrinter;
import by.andersen.utils.DtoScanner;
import by.andersen.utils.Result;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CustomerLoop {
  private final WorkspaceService workspaceService;
  private final ReservationService reservationService;

  @Autowired
  public CustomerLoop(
      WorkspaceService workspaceService,
      ReservationService reservationService
  ) {
    this.workspaceService = workspaceService;
    this.reservationService = reservationService;
  }

  public void run(User customer) {
    System.out.println("You are in customer menu");
    Scanner inputReader = new Scanner(System.in);
    String input;

    while(true) {
      System.out.printf("%s$ ", customer.getName());
      input = inputReader.nextLine();
      CustomerLoopCommand customerLoopCommand;

      try {
        customerLoopCommand = CustomerLoopCommand.valueOf(input.toUpperCase());
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid input!!!");
        CommandPrinter.printEnumValues(CustomerLoopCommand.class);
        continue;
      }

      if (customerLoopCommand == CustomerLoopCommand.EXIT) {
        return;
      }

      handleCommand(customerLoopCommand, customer.getId());
    }
  }

  private void handleCommand(CustomerLoopCommand customerLoopCommand, Long customerId) {
    Scanner inputReader = new Scanner(System.in);
    if (customerLoopCommand == CustomerLoopCommand.SHOW_SPACES) {
      Optional<PeriodDto> optionalPeriodDto = DtoScanner.scan(PeriodDto.class);

      optionalPeriodDto.ifPresent(this::showAvailableWorkspaces);
    } else if (customerLoopCommand == CustomerLoopCommand.MAKE_RESERVATION) {
      Optional<ReservationDto> reservationDto = DtoScanner.scan(ReservationDto.class);

      reservationDto.ifPresent(dto -> this.makeReservation(customerId, dto));
    } else if (customerLoopCommand == CustomerLoopCommand.VIEW_RESERVATIONS) {
      showCustomerReservations(customerId);
    } else if (customerLoopCommand == CustomerLoopCommand.CANCEL_RESERVATION) {
      System.out.print("id: ");
      Long reservationId = inputReader.nextLong();

      cancelReservation(customerId, reservationId);
    }
  }

  private void showAvailableWorkspaces(PeriodDto periodDto) {
    Result<List<Workspace>> availableWorkspaces = workspaceService.getAvailableWorkspaces(periodDto);
    System.out.println(availableWorkspaces);
  }

  private void makeReservation(Long customerId, ReservationDto reservationDto) {
    Result<Reservation> reservation = reservationService.makeReservation(customerId, reservationDto);
    System.out.println(reservation);
  }

  private void showCustomerReservations(Long customerId) {
    Result<List<Reservation>> reservations = reservationService.getUserReservations(customerId);
    System.out.println(reservations);
  }

  private void cancelReservation(Long customerId, Long reservationId) {
    Result<String> result = reservationService.cancelReservation(customerId, reservationId);
    System.out.println(result);
  }
}
