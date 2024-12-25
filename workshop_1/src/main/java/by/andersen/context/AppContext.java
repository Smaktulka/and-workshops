package by.andersen.context;

import by.andersen.repository.ReservationRepository;
import by.andersen.repository.UserRepository;
import by.andersen.repository.WorkspaceRepository;
import by.andersen.utils.PropertiesUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppContext {
  private RepositoryContext repositoryContext;
  private PropertiesUtils propertiesUtils;

  public static AppContext init() {
    RepositoryContext repositoryContext = new RepositoryContext();
    UserRepository userRepository = new UserRepository();
    ReservationRepository reservationRepository = new ReservationRepository();
    WorkspaceRepository workspaceRepository = new WorkspaceRepository();

    repositoryContext.putRepository(userRepository);
    repositoryContext.putRepository(reservationRepository);
    repositoryContext.putRepository(workspaceRepository);

    PropertiesUtils propertiesUtils = PropertiesUtils.loadAppPropertiesFile();

    Optional<Path> optionalRepositoryStateFilePath = propertiesUtils.getRepositoryStateFile();

    optionalRepositoryStateFilePath.ifPresent(
        filePath -> loadRepositoriesFromFile(repositoryContext, filePath));

    return new AppContext(repositoryContext, propertiesUtils);
  }

  public void onEnd() {
    Optional<Path> optionalRepositoryStateFilePath = propertiesUtils.getRepositoryStateFile();
    optionalRepositoryStateFilePath.ifPresent(filePath -> repositoryContext.saveToFile(filePath));
  }

  private static void loadRepositoriesFromFile(
      RepositoryContext repositoryContext,
      Path repositoryStateFilePath
  ) {
    if (Files.exists(repositoryStateFilePath)) {
      repositoryContext.loadFromFile(repositoryStateFilePath);
    } else {
      try {
        Files.createFile(repositoryStateFilePath);
      } catch (IOException e) {
        throw new IllegalArgumentException("Cannot create file " + repositoryStateFilePath, e);
      }
    }
  }
}
