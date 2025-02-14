package by.andersen.coworkingspace.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import by.andersen.coworkingspace.dto.TokensDto;
import by.andersen.coworkingspace.entity.Reservation;
import by.andersen.coworkingspace.entity.User;
import by.andersen.coworkingspace.entity.Workspace;
import by.andersen.coworkingspace.repository.RefreshTokenRepository;
import by.andersen.coworkingspace.repository.ReservationRepository;
import by.andersen.coworkingspace.repository.UserRepository;
import by.andersen.coworkingspace.repository.WorkspaceRepository;
import by.andersen.coworkingspace.service.AuthService;
import by.andersen.coworkingspace.service.WorkspaceService;
import by.andersen.coworkingspace.utils.JwtUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class ReservationControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoSpyBean
  private AuthService authService;

  @Autowired
  private WorkspaceService workspaceService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private WorkspaceRepository workspaceRepository;

  @MockitoSpyBean
  private JwtUtils jwtUtils;

  @Autowired
  ObjectMapper objectMapper;

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
    reservationRepository.deleteAll();
    workspaceRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Test
  void givenAdminUser_whenGetReservations_thenReturnReservations() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());
    reservationRepository.save(TestDtosFactory.emptyReservation());

    MvcResult mvcResult = mockMvc.perform(get("/reservation")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();
    List<Reservation> reservations = objectMapper.readValue(json, new TypeReference<>() {});

    Assertions.assertEquals(reservations.size(), 1);
  }

  @Test
  void givenCustomerUser_whenGetReservations_thenReturnForbidden() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.customerRegisterDto());

    mockMvc.perform(get("/reservation")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  void givenUserId_whenGetUserReservations_thenReturnOkAndListOfReservations() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());
    User owner = userRepository.findByName(TestDtosFactory.adminRegisterDto().getUserName()).get();
    Workspace workspace = workspaceRepository.save(TestDtosFactory.workspace());
    Reservation reservation = TestDtosFactory.reservation(owner, workspace, TestDtosFactory.periodDto());
    reservationRepository.save(reservation);

    MvcResult mvcResult = mockMvc.perform(get("/reservation/user")
            .contentType(MediaType.APPLICATION_JSON)
            .param("userId", String.valueOf(owner.getId()))
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();
    List<Reservation> reservations = objectMapper.readValue(json,new TypeReference<>() {});

    Assertions.assertEquals(reservations.size(), 1);
  }

  @Test
  void givenReservationDto_whenMakeReservation_thenReturnOkAndSaveNewReservation() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());
    Workspace workspace = workspaceRepository.save(TestDtosFactory.workspace());

    MvcResult mvcResult = mockMvc.perform(post("/reservation/make")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(TestDtosFactory.reservationDto(workspace.getId())))
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();
    Reservation reservation = objectMapper.readValue(json, Reservation.class);

    Assertions.assertEquals(reservation.getId(), reservationRepository.findAll().get(0).getId());
  }

  @Test
  void givenReservationDtoWithNonExistingWorkspace_whenMakeReservation_thenReturnWorkspaceNotFound() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());


    MvcResult mvcResult = mockMvc.perform(post("/reservation/make")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(TestDtosFactory.reservationDto(1L)))
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isNotFound())
        .andReturn();

    ResponseStatusException exception = (ResponseStatusException) mvcResult.getResolvedException();
    String expectedError = "404 NOT_FOUND \"Workspace not found\"";
    String actualError = exception.getMessage();

    Assertions.assertEquals(expectedError, actualError);
  }

  @Test
  void givenReservationDtoWithWorkspaceIdOfNotAvailableWorkspace_whenMakeReservation_thenReturnBadRequest() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());
    User owner = userRepository.findByName(TestDtosFactory.adminRegisterDto().getUserName()).get();
    Workspace workspace = workspaceRepository.save(TestDtosFactory.workspace());
    Reservation reservation = TestDtosFactory.reservation(owner, workspace, TestDtosFactory.periodDto());
    reservationRepository.save(reservation);

    MvcResult mvcResult = mockMvc.perform(post("/reservation/make")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                TestDtosFactory.reservationDtoWithPeriodDto(workspace.getId(), TestDtosFactory.periodDto())))
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isBadRequest())
        .andReturn();

    ResponseStatusException exception = (ResponseStatusException) mvcResult.getResolvedException();
    String expectedError = "400 BAD_REQUEST \"Cannot create reservation on the specified date\"";
    String actualError = exception.getMessage();

    Assertions.assertEquals(expectedError, actualError);
  }

  @Test
  void givenReservationId_whenCancelReservation_thenReturnOkAndDeleteReservation() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());
    User owner = userRepository.findByName(TestDtosFactory.adminRegisterDto().getUserName()).get();
    Workspace workspace = workspaceRepository.save(TestDtosFactory.workspace());
    Reservation reservation = TestDtosFactory.reservation(owner, workspace, TestDtosFactory.periodDto());
    reservationRepository.save(reservation);

    MvcResult mvcResult = mockMvc.perform(delete("/reservation/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .param("reservationId", String.valueOf(reservation.getId()))
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isOk())
        .andReturn();

    String expectedMessage = "Reservation is canceled";
    String actualMessage = mvcResult.getResponse().getContentAsString();

    Assertions.assertEquals(expectedMessage, actualMessage);
  }

  @Test
  void givenReservationIdAndUserIsNotOwner_whenCancelReservation_thenReturnForbidden() throws Exception {
    authService.register(TestDtosFactory.adminRegisterDto());
    User owner = userRepository.findByName(TestDtosFactory.adminRegisterDto().getUserName()).get();
    Workspace workspace = workspaceRepository.save(TestDtosFactory.workspace());
    Reservation reservation = TestDtosFactory.reservation(owner, workspace, TestDtosFactory.periodDto());
    reservationRepository.save(reservation);

    TokensDto tokensDto = authService.register(TestDtosFactory.customerRegisterDto());

    MvcResult mvcResult = mockMvc.perform(delete("/reservation/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .param("reservationId", String.valueOf(reservation.getId()))
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isForbidden())
        .andReturn();

    ResponseStatusException exception = (ResponseStatusException) mvcResult.getResolvedException();
    String expectedError = "403 FORBIDDEN \"Cannot cancel someone's else reservation\"";
    String actualError = exception.getMessage();

    Assertions.assertEquals(expectedError, actualError);
  }

  @Test
  void givenReservationId_whenCancelReservation_thenReturnReservationNoFound() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());

    MvcResult mvcResult = mockMvc.perform(delete("/reservation/cancel")
            .contentType(MediaType.APPLICATION_JSON)
            .param("reservationId", String.valueOf(1L))
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isNotFound())
        .andReturn();

    ResponseStatusException exception = (ResponseStatusException) mvcResult.getResolvedException();
    String expectedError = "404 NOT_FOUND \"Reservation not found\"";
    String actualError = exception.getMessage();

    Assertions.assertEquals(expectedError, actualError);
  }
}
