package by.andersen.utils;

import by.andersen.utils.PropertiesUtils.PropertyName;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PropertiesUtilsTest {
  @Test
  public void givenValidAppPropertiesFileName_whenLoadAppPropertiesFile_thenReturnPropertiesUtilsObject() {
    PropertiesUtils propertiesUtils = PropertiesUtils.loadAppPropertiesFile("test.app.properties");

    Assertions.assertNotNull(propertiesUtils);
  }

  @Test
  public void givenInvalidAppPropertiesFileName_whenLoadAppPropertiesFile_thenThrowIllegalArgumentException() {
    Exception e = Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> PropertiesUtils.loadAppPropertiesFile(Mockito.anyString())
    );

    String expectedMessage = "Cannot read properties file";
    String actualMessage = e.getMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void givenPropertiesUtils_whenGetRepositoryStateFilePath_thenReturnValidRepositoryStateFilePath() {
    String appPropertiesFileName = "test.app.properties";
    PropertiesUtils propertiesUtils = PropertiesUtils.loadAppPropertiesFile(appPropertiesFileName);

    Properties properties = getPropertiesObject(appPropertiesFileName);

    String expectedPropertyValue = properties.getProperty(PropertyName.REPOSITORY_STATE_FILEPATH);
    String actualPropertyValue = propertiesUtils.getRepositoryStateFilePath().get().getFileName().toString();

    Assertions.assertEquals(actualPropertyValue, expectedPropertyValue);
  }

  @Test
  public void givenPropertiesUtils_whenGetRepositoryStateFilePath_thenReturnEmptyRepositoryStateFilePath() {
    String appPropertiesFileName = "empty.app.properties";
    PropertiesUtils propertiesUtils = PropertiesUtils.loadAppPropertiesFile(appPropertiesFileName);
    Properties properties = getPropertiesObject(appPropertiesFileName);

    Optional<Path> emptyPath = propertiesUtils.getRepositoryStateFilePath();

    Assertions.assertTrue(emptyPath.isEmpty());
    Assertions.assertNull(properties.getProperty(PropertyName.REPOSITORY_STATE_FILEPATH));
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

  private static Properties getPropertiesObject(String appPropertiesFileName) {
    Properties properties = new Properties();
    Path appPropertiesFilePath = getAppPropertiesFilePath(appPropertiesFileName);

    try (InputStream inStream = Files.newInputStream(appPropertiesFilePath)) {
      properties.load(inStream);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return properties;
  }
}
