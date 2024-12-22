package by.andersen.loops;

import by.andersen.context.AppContext;
import by.andersen.dto.WorkspaceDto;
import by.andersen.entity.Reservation;
import by.andersen.entity.User;
import by.andersen.entity.Workspace;
import by.andersen.enums.command.AdminLoopCommand;
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

public class AdminLoop {
  private final WorkspaceService workspaceService;
  private final ReservationService reservationService;
  private final String WORKSPACES = "workspaces";
  private final String RESERVATIONS = "reservations";

  public AdminLoop(AppContext appContext) {
    WorkspaceRepository workspaceRepository = (WorkspaceRepository) appContext
        .getRepositoryContext()
        .getRepository(WorkspaceRepository.class);
    ReservationRepository reservationRepository = (ReservationRepository) appContext
        .getRepositoryContext()
        .getRepository(ReservationRepository.class);
    this.workspaceService = new WorkspaceService(workspaceRepository, reservationRepository);
    this.reservationService =
        new ReservationService(reservationRepository, workspaceRepository);
  }

  public void run(User admin) {
    System.out.println("You are in admin menu");
    Scanner inputReader = new Scanner(System.in);
    String input;

    while(true) {
      System.out.printf("%s# ", admin.getName());
      input = inputReader.nextLine();
      AdminLoopCommand adminLoopCommand;

      try {
        adminLoopCommand = AdminLoopCommand.valueOf(input.toUpperCase());
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid input!!!");
        CommandPrinter.printEnumValues(AdminLoopCommand.class);
        continue;
      }

      if (adminLoopCommand == AdminLoopCommand.EXIT) {
        return;
      }

      handleCommand(adminLoopCommand);
    }
  }

  private void handleCommand(AdminLoopCommand adminLoopCommand) {
    Scanner inputReader = new Scanner(System.in);
    if (adminLoopCommand == AdminLoopCommand.ADD_SPACE) {
      Optional<WorkspaceDto> optionalWorkspaceDto = DtoScanner.scan(WorkspaceDto.class);
      optionalWorkspaceDto.ifPresent(this::addWorkspace);
    } else if (adminLoopCommand == AdminLoopCommand.SHOW) {
      System.out.print("(workspaces/reservations): ");
      String input = inputReader.nextLine();
      showWorkspacesOrReservation(input);
    } else if (adminLoopCommand == AdminLoopCommand.REMOVE_SPACE) {
      System.out.print("id: ");
      Long workspaceId = inputReader.nextLong();
      removeWorkspace(workspaceId);
    }
  }

  private void addWorkspace(WorkspaceDto workspaceDto) {
    Result<String> result = workspaceService.addWorkspace(workspaceDto);
    System.out.println(result);
  }

  private void showWorkspacesOrReservation(String input) {
    if (input.equals(WORKSPACES)) {
      showWorkspaces();
    } else if (input.equals(RESERVATIONS)){
      showReservations();
    }
  }

  private void removeWorkspace(Long workspaceId) {
    Result<String> result = workspaceService.removeWorkspaceById(workspaceId);
    System.out.println(result);
  }

  private void showWorkspaces() {
    Result<List<Workspace>> result = workspaceService.getWorkspaces();
    System.out.println(result);
  }

  private void showReservations() {
    Result<List<Reservation>> result = reservationService.getReservations();
    System.out.println(result);
  }
}
