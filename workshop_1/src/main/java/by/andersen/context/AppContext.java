package by.andersen.context;

import by.andersen.repository.ReservationRepository;
import by.andersen.repository.UserRepository;
import by.andersen.repository.WorkspaceRepository;
import by.andersen.utils.PropertiesUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

    Optional<File> optionalRepositoryStateFile = propertiesUtils.getRepositoryStateFile();

    optionalRepositoryStateFile.ifPresent(
        file -> loadRepositoriesFromFile(repositoryContext, file));

    return new AppContext(repositoryContext, propertiesUtils);
  }

  public void onEnd() {
    Optional<File> optionalRepositoryStateFile = propertiesUtils.getRepositoryStateFile();
    optionalRepositoryStateFile.ifPresent(file -> repositoryContext.saveToFile(file.getPath()));
  }

  private static void loadRepositoriesFromFile(RepositoryContext repositoryContext, File repositoryStateFile) {
    if (repositoryStateFile.exists()) {
      repositoryContext.loadFromFile(repositoryStateFile.getPath());
    } else {
      try {
        Files.createFile(repositoryStateFile.toPath());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
