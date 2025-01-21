package by.andersen.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertiesUtils {
  private Properties properties;

  public static PropertiesUtils loadAppPropertiesFile(String appPropertiesFileName) {
    Properties properties = new Properties();
    Path appPropertiesFile = getAppPropertiesFilePath(appPropertiesFileName);
    try (InputStream inStream = Files.newInputStream(appPropertiesFile)) {
      properties.load(inStream);
    } catch (IOException e) {
      throw new IllegalArgumentException("Cannot read properties file", e);
    }

    return new PropertiesUtils(properties);
  }

  public Optional<Path> getRepositoryStateFilePath() {
    Optional<String> propertyOpt = getProperty(PropertyName.REPOSITORY_STATE_FILEPATH);

    if (propertyOpt.isEmpty()) {
      return Optional.empty();
    }

    URI resourcesUri = getResourceUri();
    Path resourcesPath = Paths.get(resourcesUri);
    Path repositoryStateFilePath =
        Paths.get(resourcesPath.toString(), propertyOpt.get());

    return Optional.of(repositoryStateFilePath);
  }

  public Optional<String> getProperty(String propertyName) {
    String property = properties.getProperty(propertyName);

    if (property == null) {
      return Optional.empty();
    }

    return Optional.of(property);
  }

  private URI getResourceUri() {
    try {
      return ClassLoader.getSystemResource("").toURI();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private static Path getAppPropertiesFilePath(String appPropertiesFileName) {
    URI appPropertiesUri;
    try {
      appPropertiesUri = ClassLoader.getSystemResource(appPropertiesFileName).toURI();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Cannot get properties file path", e);
    }

    return Paths.get(appPropertiesUri);
  }

  public static class PropertyName {
    public static final String REPOSITORY_STATE_FILEPATH = "repository.state.filepath";
    public static final String SQL_DB_URL = "sql.db.url";
    public static final String SQL_DB_USERNAME = "sql.db.username";
    public static final String SQL_DB_PASSWORD = "sql.db.password";
    public static final String SQL_DB_SCHEMA = "sql.db.schema";
  }
}
