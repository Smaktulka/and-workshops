package by.andersen.context;

import by.andersen.repository.Repository;
import java.util.HashMap;
import java.util.Map;

public class RepositoryContext {
  private final Map<Class<?>, Repository<?, ?>> repositoryMap = new HashMap<>();

  public <T, ID> void putRepository(Repository<T, ID> repository) {
    repositoryMap.put(repository.getClass(), repository);
  }

  public <T, ID> Repository<T, ID> getRepository(Class<? extends Repository<T, ID>> repositoryClass) {
    return (Repository<T, ID>) repositoryMap.get(repositoryClass);
  }
}
