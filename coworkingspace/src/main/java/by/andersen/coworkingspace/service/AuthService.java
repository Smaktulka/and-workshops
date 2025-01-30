package by.andersen.coworkingspace.service;

import by.andersen.coworkingspace.dto.LoginDto;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.repository.UserRepository;
import by.andersen.coworkingspace.utils.PasswordHashUtils;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {
  private final UserRepository userRepository;

  @Autowired
  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User login(LoginDto loginDto) {
    Optional<User> optionalUser = userRepository.findByName(loginDto.getUserName());
    if (optionalUser.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, "User with name " + loginDto.getUserName() + " is not found");
    }

    boolean isPasswordValid = PasswordHashUtils.
        verifyPassword(loginDto.getPassword(), optionalUser.get().getPasswordHash());

    if (!isPasswordValid) {
      throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Invalid password");
    }

    return optionalUser.get();
  }
}
