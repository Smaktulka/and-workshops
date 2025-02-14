package by.andersen.coworkingspace.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.andersen.coworkingspace.dto.TokensDto;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.repository.RefreshTokenRepository;
import by.andersen.coworkingspace.repository.UserRepository;
import by.andersen.coworkingspace.service.AuthService;
import by.andersen.coworkingspace.utils.JwtUtils;
import by.andersen.coworkingspace.utils.PasswordHashUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class AuthControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoSpyBean
  private AuthService authService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @MockitoSpyBean
  private JwtUtils jwtUtils;

  static PostgreSQLContainer<?> postgreSqlContainer = new PostgreSQLContainer<>("postgres:15.3-alpine")
      .withDatabaseName("test-db")
      .withUsername("testUser")
      .withPassword("testPass")
      .withInitScript("create_space_schema.sql")
      .withReuse(true);

  @BeforeAll
  static void startPsqlContainer() {
    postgreSqlContainer.start();
  }

  @DynamicPropertySource
  static void registerPgProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSqlContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSqlContainer::getUsername);
    registry.add("spring.datasource.password", postgreSqlContainer::getPassword);
  }

  @AfterEach
  public void cleanUpDb() {
    refreshTokenRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void givenRegisterDto_whenRegister_thenReturnTokensDtoAndSaveUserAndSaveRefreshToken() throws Exception {
    MvcResult mvcResult = mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(TestDtosFactory.adminRegisterDto())))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();
    TokensDto tokens = new ObjectMapper().readValue(json, TokensDto.class);

    Assertions.assertTrue(userRepository.findByName(TestDtosFactory.adminRegisterDto().getUserName()).isPresent());
    Assertions.assertTrue(refreshTokenRepository.getByRefreshToken(tokens.getRefreshToken()).isPresent());
  }

  @Test
  void givenRegisterDtoWithUsernameThatIsAlreadyTakes_whenRegister_thenReturnForbidden() throws Exception {
    userRepository.save(TestDtosFactory.userWithInvalidPasswordHash());

    MvcResult mvcResult = mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(TestDtosFactory.adminRegisterDto())))
        .andExpect(status().isForbidden())
        .andReturn();

    ResponseStatusException exception = (ResponseStatusException) mvcResult.getResolvedException();
    String expectedError = "403 FORBIDDEN \"User name is taken\"";
    String actualError = exception.getMessage();

    Assertions.assertEquals(expectedError, actualError);
  }

  @Test
  void givenLoginDto_whenLoginAndUserAlreadyRegistered_thenReturnTokensDto() throws Exception {
    String passwordHash = PasswordHashUtils.hash(TestDtosFactory.loginDto().getPassword());
    User user = TestDtosFactory.user(passwordHash);
    userRepository.save(user);

    TokensDto tokensDto = jwtUtils.generateTokens(user);

    mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(TestDtosFactory.loginDto())))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(content().json(new ObjectMapper().writeValueAsString(tokensDto)));
  }

  @Test
  void givenLoginDtoWithUsernameOfNonExistingUser_whenLogin_thenReturnNotFound() throws Exception {
    MvcResult mvcResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(TestDtosFactory.loginDto())))
        .andExpect(status().isNotFound())
        .andReturn();

    ResponseStatusException exception = (ResponseStatusException) mvcResult.getResolvedException();
    String expectedError = "404 NOT_FOUND \"User with name " + TestDtosFactory.loginDto().getUserName() + " is not found\"";
    String actualError = exception.getMessage();

    Assertions.assertEquals(expectedError, actualError);
  }

  @Test
  void givenLoginDtoWithInvalidPassword_whenLogin_thenReturnBadRequest() throws Exception {
    String passwordHash = PasswordHashUtils.hash(TestDtosFactory.loginDto().getPassword());
    User user = TestDtosFactory.user(passwordHash);
    userRepository.save(user);

    MvcResult mvcResult = mockMvc.perform(post("/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(TestDtosFactory.loginDtoWithInvalidPassword())))
        .andExpect(status().isBadRequest())
        .andReturn();

    ResponseStatusException exception = (ResponseStatusException) mvcResult.getResolvedException();
    String expectedError = "400 BAD_REQUEST \"Invalid password\"";
    String actualError = exception.getMessage();

    Assertions.assertEquals(expectedError, actualError);
  }

  @Test
  void givenValidRefreshToken_whenRefreshToken_thenReturnTokensDto() throws Exception {
    MockHttpServletResponse response = new MockHttpServletResponse();
    User user = TestDtosFactory.userWithInvalidPasswordHash();
    user = userRepository.save(user);

    TokensDto tokensDto = jwtUtils.generateTokens(user);
    refreshTokenRepository.save(TestDtosFactory.token(tokensDto.getRefreshToken(), user));

    MvcResult mvcResult = mockMvc.perform(post("/auth/refresh-token")
            .param("refresh_token", tokensDto.getRefreshToken())
            .contentType(MediaType.APPLICATION_JSON)
          .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isOk())
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();

    TokensDto refreshed = new ObjectMapper().readValue(json, TokensDto.class);

    Assertions.assertTrue(jwtUtils.validateToken(refreshed.getAccessToken(), user.getName()));
    Assertions.assertTrue(jwtUtils.validateToken(refreshed.getRefreshToken(), user.getName()));
  }

  @Test
  void givenEmptyAuthorizationHeader_whenRefreshToken_thenReturnForbidden() throws Exception {
    mockMvc.perform(post("/auth/refresh-token")
            .param("refresh_token", "refreshToken")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "empty"))
        .andExpect(status().isForbidden());
  }

  @Test
  void givenRefreshTokenWithUserNameOfAnotherUser_whenRefreshToken_thenReturnForbiddenInvalidToken() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());;

    String refreshTokenWithUserNameOfAnotherUser =
        jwtUtils.generateRefreshToken(new HashMap<>(), TestDtosFactory.anotherUser());
    User anotherUser = userRepository.save(TestDtosFactory.anotherUser());
    refreshTokenRepository.save(TestDtosFactory.token(refreshTokenWithUserNameOfAnotherUser, anotherUser));

    MvcResult mvcResult = mockMvc.perform(post("/auth/refresh-token")
            .param("refresh_token", refreshTokenWithUserNameOfAnotherUser)
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isForbidden())
        .andReturn();

    ResponseStatusException exception = (ResponseStatusException) mvcResult.getResolvedException();
    String expectedError = "403 FORBIDDEN \"Refresh token is invalid\"";
    String actualError = exception.getMessage();

    Assertions.assertEquals(expectedError, actualError);
  }
}
