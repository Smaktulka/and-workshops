package by.andersen.coworkingspace.repository;

import by.andersen.coworkingspace.entity.RefreshToken;
import by.andersen.coworkingspace.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
  void deleteByUser(User user);

  Optional<RefreshToken> getByRefreshToken(String refreshToken);
}
