package by.andersen.service;

import static org.mockito.Mockito.when;

import by.andersen.dto.LoginDto;
import by.andersen.entity.User;
import by.andersen.enums.UserRole;
import by.andersen.repository.UserRepository;
import by.andersen.utils.PasswordHashUtils;
import by.andersen.utils.Result;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

public class AuthServiceTest {
  private AuthService authService;
  @Mock
  private UserRepository userRepository;

  @BeforeEach
  public void setUp() {
    this.userRepository = Mockito.mock(UserRepository.class);
    this.authService = new AuthService(userRepository);
  }

  @Test
  public void givenValidLoginDto_whenLogin_thenReturnResultWithUserObject() {
    LoginDto loginDto = new LoginDto("test", "1234".toCharArray());

    User user = user();

    when(userRepository.getByName(loginDto.getUserName()))
        .thenReturn(Optional.of(user));

    try (MockedStatic<PasswordHashUtils> passHashUtils = Mockito.mockStatic(PasswordHashUtils.class)) {
      passHashUtils.when(() -> PasswordHashUtils.verifyPassword(loginDto.getPassword(), user.getPasswordHash()))
          .thenReturn(true);

      Result<User> result = authService.login(loginDto);

      Assertions.assertEquals(result.getResultValue(), user);
    }
  }

  @Test
  public void givenLoginDtoWithInvalidUserName_whenLogin_thenReturnResultWithUserNotFoundError() {
    LoginDto loginDto = new LoginDto("non-existing", "1234".toCharArray());

    when(userRepository.getByName(loginDto.getUserName()))
        .thenReturn(Optional.empty());

    Result<User> result = authService.login(loginDto);

    String expectedMessage = "User with name " + loginDto.getUserName() + " is not found";
    String actualMessage = result.getErrorMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void givenLoginDtoWithInvalidPassword_whenLogin_thenReturnResultWithInvalidPasswordError() {
    LoginDto loginDto = new LoginDto("test", "1234".toCharArray());

    User user = user();

    when(userRepository.getByName(loginDto.getUserName()))
        .thenReturn(Optional.of(user));

    try (MockedStatic<PasswordHashUtils> passHashUtils = Mockito.mockStatic(PasswordHashUtils.class)) {
      passHashUtils.when(() -> PasswordHashUtils.verifyPassword(loginDto.getPassword(), user.getPasswordHash()))
          .thenReturn(false);

      Result<User> result = authService.login(loginDto);

      String expectedMessage = "Invalid password";
      String actualMessage = result.getErrorMessage();

      Assertions.assertEquals(actualMessage, expectedMessage);
    }
  }

  public static User user() {
    return User.builder()
        .id(1L)
        .name("test")
        .role(UserRole.ADMIN)
        .passwordHash("hash")
        .build();
  }
}
