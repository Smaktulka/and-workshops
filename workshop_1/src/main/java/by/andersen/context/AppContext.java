package by.andersen.context;

import by.andersen.entity.Reservation;
import by.andersen.entity.User;
import by.andersen.entity.Workspace;
import by.andersen.repository.ReservationRepository;
import by.andersen.repository.UserRepository;
import by.andersen.repository.WorkspaceRepository;
import by.andersen.utils.PropertiesUtils;
import by.andersen.utils.PropertiesUtils.PropertyName;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.postgresql.ds.PGSimpleDataSource;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AppContext {
  private RepositoryContext repositoryContext;
  private PropertiesUtils propertiesUtils;

  public static AppContext init() {
    RepositoryContext repositoryContext = new RepositoryContext();


    PropertiesUtils propertiesUtils = PropertiesUtils.loadAppPropertiesFile("app.properties");
    SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();

    UserRepository userRepository = new UserRepository(sessionFactory);
    ReservationRepository reservationRepository = new ReservationRepository(sessionFactory);
    WorkspaceRepository workspaceRepository = new WorkspaceRepository(sessionFactory);

    repositoryContext.putRepository(userRepository);
    repositoryContext.putRepository(reservationRepository);
    repositoryContext.putRepository(workspaceRepository);

    Optional<Path> optionalRepositoryStateFilePath = propertiesUtils.getRepositoryStateFilePath();

    optionalRepositoryStateFilePath.ifPresent(
        filePath -> {
          loadRepositoriesFromFile(repositoryContext, filePath);
          setUpOnEndHookToSaveRepositoriesToFile(repositoryContext, filePath);
        }
    );

    return new AppContext(repositoryContext, propertiesUtils);
  }

  public static void setUpOnEndHookToSaveRepositoriesToFile(RepositoryContext repositoryContext, Path filePath) {
    Runnable shutdownHook = () -> repositoryContext.saveToFile(filePath);
    Runtime.getRuntime().addShutdownHook(new Thread(shutdownHook));
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

  private static PGSimpleDataSource generateDataSource(PropertiesUtils propertiesUtils) {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    propertiesUtils.getProperty(PropertyName.SQL_DB_URL)
        .ifPresent(dataSource::setUrl);
    propertiesUtils.getProperty(PropertyName.SQL_DB_USERNAME)
        .ifPresent(dataSource::setUser);
    propertiesUtils.getProperty(PropertyName.SQL_DB_PASSWORD)
        .ifPresent(dataSource::setPassword);
    propertiesUtils.getProperty(PropertyName.SQL_DB_SCHEMA)
        .ifPresent(dataSource::setCurrentSchema);


    return dataSource;
  }
}
