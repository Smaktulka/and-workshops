package by.andersen.loops;

import by.andersen.context.AppContext;
import by.andersen.dto.LoginDto;
import by.andersen.entity.User;
import by.andersen.enums.command.StartLoopCommand;
import by.andersen.repository.UserRepository;
import by.andersen.service.AuthService;
import by.andersen.utils.CommandPrinter;
import by.andersen.utils.ConsolePasswordUtils;
import by.andersen.utils.Result;
import java.util.Scanner;

public class StartLoop {
  private final AppContext appContext;
  private final AuthService authService;

  public StartLoop(AppContext appContext) {
    this.appContext = appContext;
    UserRepository userRepository =
        (UserRepository) appContext.getRepositoryContext().getRepository(UserRepository.class);
    this.authService = new AuthService(userRepository);
  }

  public void run() {
    System.out.println("Hello there!!!");
    Scanner inputReader = new Scanner(System.in);
    String input;

    while(true) {
      System.out.print("> ");
      input = inputReader.nextLine();
      StartLoopCommand startLoopCommand;

      try {
        startLoopCommand = StartLoopCommand.valueOf(input.toUpperCase());
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid input!!!");
        CommandPrinter.printEnumValues(StartLoopCommand.class);
        continue;
      }

      if (startLoopCommand == StartLoopCommand.EXIT) {
        return;
      }

      handleCommand(startLoopCommand);
    }
  }

  private void handleCommand(StartLoopCommand startLoopCommand) {
    if (startLoopCommand == StartLoopCommand.LOGIN) {
      Result<User> result = login();
      if (result.isEmpty()) {
        System.out.println(result.getErrorMessage());
      } else {
        User user = result.getResultValue();
        switch (user.getRole()) {
          case CUSTOMER:
            new CustomerLoop(appContext).run(user);
            break;
          case ADMIN:
            new AdminLoop(appContext).run(user);
        }
      }
    }
  }

  private Result<User> login() {
    Scanner inputReader = new Scanner(System.in);
    System.out.println("Please enter your user name:");
    String userName = inputReader.nextLine();
    System.out.println("Please enter your password:");
    char[] password = ConsolePasswordUtils.readPassword();
    LoginDto loginDto = new LoginDto(userName, password);
    return authService.login(loginDto);
  }
}
