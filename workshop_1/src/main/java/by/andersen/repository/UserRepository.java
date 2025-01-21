package by.andersen.repository;

import by.andersen.entity.User;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class UserRepository extends JdbcRepository<User, Long> {
  public UserRepository(DataSource dataSource, String currentSchema, String tableName, Class<User> entityClass) {
    super(dataSource, currentSchema, tableName, entityClass);
  }

  public Optional<User> getByName(String name) {
    List<User> users = this.findAll();
    return users.stream()
        .filter(user -> user.getName().equals(name))
        .findAny();
  }
}