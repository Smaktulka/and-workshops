package by.andersen.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

public interface Repository<T extends Serializable, ID> {
  void save(T entity);
  Optional<T> findById(ID id);
  List<T> findAll();
  void delete(ID id);
}