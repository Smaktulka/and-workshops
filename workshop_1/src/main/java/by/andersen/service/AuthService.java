package by.andersen.service;

import by.andersen.dto.LoginDto;
import by.andersen.entity.User;
import by.andersen.repository.UserRepository;
import by.andersen.utils.PasswordHashUtils;
import by.andersen.utils.Result;
import java.util.Optional;

public class AuthService {
  private final UserRepository userRepository;

  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Result<User> login(LoginDto loginDto) {
    Optional<User> optionalUser = userRepository.getByName(loginDto.getUserName());
    if (optionalUser.isEmpty()) {
      return Result.error("User with name " + loginDto.getUserName() + " is not found");
    }

    boolean isPasswordValid = PasswordHashUtils
        .verifyPassword(loginDto.getPassword(), optionalUser.get().getPasswordHash());

    if (!isPasswordValid) {
      return Result.error("Invalid password");
    }

    return Result.ok(optionalUser.get());
  }
}
