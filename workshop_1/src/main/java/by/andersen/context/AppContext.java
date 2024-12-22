package by.andersen.context;

import by.andersen.repository.ReservationRepository;
import by.andersen.repository.UserRepository;
import by.andersen.repository.WorkspaceRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppContext {
  private RepositoryContext repositoryContext;

  public static AppContext init() {
    RepositoryContext repositoryContext = new RepositoryContext();
    UserRepository userRepository = new UserRepository();
    ReservationRepository reservationRepository = new ReservationRepository();
    WorkspaceRepository workspaceRepository = new WorkspaceRepository();

    repositoryContext.putRepository(userRepository);
    repositoryContext.putRepository(reservationRepository);
    repositoryContext.putRepository(workspaceRepository);

    return new AppContext(repositoryContext);
  }
}
