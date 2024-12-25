package by.andersen.repository;

import by.andersen.entity.Identifiable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

@Getter
public class InMemoryRepository<T extends Serializable, ID> implements Repository<T, ID>, Serializable {
  private final Map<ID, T> idEntityMap = new HashMap<>();
  private volatile ID lastId;

  @Override
  public void save(T entity) {
    ID id = ((Identifiable<ID>) entity).getId();
    this.setLastId(id);
    idEntityMap.put(id, entity);
  }

  @Override
  public Optional<T> findById(ID id) {
    return Optional.ofNullable(idEntityMap.get(id));
  }

  @Override
  public List<T> findAll() {
    return new ArrayList<>(idEntityMap.values());
  }

  @Override
  public void delete(ID id) {
    idEntityMap.remove(id);
  }

  private void setLastId(ID id) {
    this.lastId = id;
  }
}
