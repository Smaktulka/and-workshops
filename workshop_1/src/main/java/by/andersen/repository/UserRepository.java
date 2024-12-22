package by.andersen.repository;

import by.andersen.entity.User;
import by.andersen.enums.UserRole;
import by.andersen.utils.PasswordHashUtils;
import java.util.List;
import java.util.Optional;

public class UserRepository extends InMemoryRepository<User, Long> {

  {
    String adminHashedPassword = PasswordHashUtils.hash("1234".toCharArray());
    String customerHashedPassword = PasswordHashUtils.hash("1234".toCharArray());
    User admin = User.builder()
        .id(1L)
        .name("admin")
        .role(UserRole.ADMIN)
        .passwordHash(adminHashedPassword)
        .build();
    User customer = User.builder()
        .id(2L)
        .name("yauheni")
        .role(UserRole.CUSTOMER)
        .passwordHash(customerHashedPassword)
        .build();

    this.save(admin);
    this.save(customer);
  }

  public Optional<User> getByName(String name) {
    List<User> users = this.findAll();
    return users.stream()
        .filter(user -> user.getName().equals(name))
        .findAny();
  }
}