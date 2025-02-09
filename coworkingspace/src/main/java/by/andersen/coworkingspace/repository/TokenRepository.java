package by.andersen.coworkingspace.repository;

import by.andersen.coworkingspace.entity.Token;
import by.andersen.coworkingspace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
  void deleteByUser(User user);
}
