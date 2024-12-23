package by.andersen.context;

import by.andersen.repository.Repository;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class RepositoryContext {
  private final Map<Class<?>, Repository<? extends Serializable, ?>> repositoryMap = new HashMap<>();

  public <T extends Serializable, ID> void putRepository(Repository<T, ID> repository) {
    repositoryMap.put(repository.getClass(), repository);
  }

  public <T extends Serializable, ID> Repository<T, ID> getRepository(Class<? extends Repository<T, ID>> repositoryClass) {
    return (Repository<T, ID>) repositoryMap.get(repositoryClass);
  }

  public void saveToFile(String filePath) {
    try (ObjectOutputStream oos =
        new ObjectOutputStream(Files.newOutputStream(Paths.get(filePath)))) {
      oos.writeObject(repositoryMap);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public void loadFromFile(String filePath) {
    try (ObjectInputStream ois =
        new ObjectInputStream(Files.newInputStream(Paths.get(filePath)))) {
      Map<Class<?>, Repository<?, ?>> loadedRepositories = (Map<Class<?>, Repository<?, ?>>) ois.readObject();
      repositoryMap.clear();
      repositoryMap.putAll(loadedRepositories);
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
