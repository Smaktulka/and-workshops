package by.andersen.utils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PropertiesUtils {
  private Properties properties;
  private static final String APP_PROPERTIES_FILENAME = "app.properties";
  private static final String REPOSITORY_STATE_FILEPATH = "repository.state.filepath";

  public static PropertiesUtils loadAppPropertiesFile() {
    Properties properties = new Properties();
    File appPropertiesFile = getAppPropertiesFile();
    try {
      properties.load(Files.newInputStream(appPropertiesFile.toPath()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return new PropertiesUtils(properties);
  }

  public Optional<File> getRepositoryStateFile() {
    if (properties.getProperty(REPOSITORY_STATE_FILEPATH) == null) {
      return Optional.empty();
    }

    URI repositoryStateFileUri;
    try {
      repositoryStateFileUri = ClassLoader.getSystemResource("").toURI();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    String repositoryStateFilePath = Paths.get(repositoryStateFileUri).toString();

    String fileSeparator = FileSystems.getDefault().getSeparator();
    repositoryStateFilePath += fileSeparator + properties.getProperty(REPOSITORY_STATE_FILEPATH);

    return Optional.of(new File(repositoryStateFilePath));
  }

  private static File getAppPropertiesFile() {
    URI appPropertiesUri;
    try {
      appPropertiesUri = ClassLoader.getSystemResource(APP_PROPERTIES_FILENAME).toURI();
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    String appPropertiesPath = Paths.get(appPropertiesUri).toString();
    return new File(appPropertiesPath);
  }
}
