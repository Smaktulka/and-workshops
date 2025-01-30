package by.andersen.repository;

import by.andersen.entity.User;
import java.util.List;
import java.util.Optional;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends HibernateRepository<User, Long> {
  @Autowired
  public UserRepository(SessionFactory sessionFactory) {
    super(sessionFactory, User.class);
  }

  public Optional<User> getByName(String name) {
    List<User> users = this.findAll();
    return users.stream()
        .filter(user -> user.getName().equals(name))
        .findAny();
  }
}