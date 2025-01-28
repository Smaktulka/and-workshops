package by.andersen.repository;

import by.andersen.entity.User;
import java.util.List;
import java.util.Optional;
import org.hibernate.SessionFactory;

public class UserRepository extends HibernateRepository<User, Long> {
  public UserRepository(
      SessionFactory sessionFactory,
      Class<User> entityClass
  ) {
    super(sessionFactory, entityClass);
  }

  public Optional<User> getByName(String name) {
    List<User> users = this.findAll();
    return users.stream()
        .filter(user -> user.getName().equals(name))
        .findAny();
  }
}