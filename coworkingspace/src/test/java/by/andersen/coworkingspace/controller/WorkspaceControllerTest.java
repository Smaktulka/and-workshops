package by.andersen.coworkingspace.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.fasterxml.jackson.core.type.TypeReference;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
public class WorkspaceControllerTest {
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
  void givenAdminUser_whenGetWorkspaces_thenReturnWorkspaces() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());
    workspaceRepository.save(TestDtosFactory.workspace());

    MvcResult mvcResult = mockMvc.perform(get("/workspace")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();
    List<Workspace> workspaces = objectMapper.readValue(json, List.class);

    Assertions.assertEquals(workspaces.size(), 1);
  }

  @Test
  void givenCustomerUser_whenGetWorkspaces_thenReturnForbidden() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.customerRegisterDto());

    mockMvc.perform(get("/workspace")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  void givenAdminUserAndWorkspaceId_whenRemoveWorkspace_thenReturnOk() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());
    Workspace workspace = workspaceRepository.save(TestDtosFactory.workspace());

    MvcResult mvcResult = mockMvc.perform(delete("/workspace/remove")
            .contentType(MediaType.APPLICATION_JSON)
            .param("workspaceId", String.valueOf(workspace.getId()))
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isOk())
        .andReturn();

    String expectedMessage = "Workspace is removed";
    String actualMessage = mvcResult.getResponse().getContentAsString();

    Assertions.assertEquals(expectedMessage, actualMessage);
    Assertions.assertTrue(workspaceRepository.findAll().isEmpty());
  }

  @Test
  void givenCustomerUser_whenRemoveWorkspace_thenReturnForbidden() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.customerRegisterDto());

    mockMvc.perform(delete("/workspace/remove")
            .param("workspaceId", String.valueOf(1))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  void givenAdminUserAndWorkspaceIdOfNonExisting_whenRemoveWorkspace_thenReturnWorkspaceNotFound() throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());

    MvcResult mvcResult = mockMvc.perform(delete("/workspace/remove")
            .contentType(MediaType.APPLICATION_JSON)
            .param("workspaceId", String.valueOf(1))
            .header("Authorization", "Bearer " + tokensDto.getAccessToken()))
        .andExpect(status().isNotFound())
        .andReturn();

    ResponseStatusException exception = (ResponseStatusException) mvcResult.getResolvedException();
    String expectedError = "404 NOT_FOUND \"Workspace not found\"";
    String actualError = exception.getMessage();

    Assertions.assertEquals(expectedError, actualError);
  }

  @Test
  void givenAnyUserAndPeriodDto_whenGetAvailableWorkspaces_thenReturnOkAndListOfAvailableWorkspaces()
      throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());
    workspaceRepository.save(TestDtosFactory.workspace());

    MvcResult mvcResult = mockMvc.perform(get("/workspace/available")
            .header("Authorization", "Bearer " + tokensDto.getAccessToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(TestDtosFactory.periodDto())))
        .andExpect(status().isOk())
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();
    List<Workspace> actualAvailableWorkspaces = objectMapper.readValue(json, new TypeReference<>() {});
    List<Workspace> expectedAvailableWorkspaces = workspaceService.getAvailableWorkspacesForPeriod(TestDtosFactory.periodDto());
    Assertions.assertEquals(actualAvailableWorkspaces, expectedAvailableWorkspaces);
  }

  @Test
  void givenAnyUserAndReservedPeriodDto_whenGetAvailableWorkspaces_thenReturnOkAndEmptyListOfWorkspaces()
      throws Exception {
    TokensDto tokensDto = authService.register(TestDtosFactory.adminRegisterDto());
    User owner = userRepository.findByName(TestDtosFactory.adminRegisterDto().getUserName()).get();
    Workspace workspace = workspaceRepository.save(TestDtosFactory.workspace());
    Reservation reservation = TestDtosFactory.reservation(owner, workspace, TestDtosFactory.periodDto());
    reservationRepository.save(reservation);

    MvcResult mvcResult = mockMvc.perform(get("/workspace/available")
            .header("Authorization", "Bearer " + tokensDto.getAccessToken())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(TestDtosFactory.periodDto())))
        .andExpect(status().isOk())
        .andDo(MockMvcResultHandlers.print())
        .andReturn();

    String json = mvcResult.getResponse().getContentAsString();
    List<Workspace> actualAvailableWorkspaces = objectMapper.readValue(json, new TypeReference<>() {});
    Assertions.assertTrue(actualAvailableWorkspaces.isEmpty());
  }
}
