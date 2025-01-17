package by.andersen.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import by.andersen.dto.PeriodDto;
import by.andersen.dto.WorkspaceDto;
import by.andersen.entity.Workspace;
import by.andersen.enums.WorkspaceType;
import by.andersen.repository.ReservationRepository;
import by.andersen.repository.WorkspaceRepository;
import by.andersen.utils.Result;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class WorkspaceServiceTest {
  private WorkspaceService workspaceService;
  @Mock
  private ReservationRepository reservationRepository;
  @Mock
  private WorkspaceRepository workspaceRepository;

  @BeforeEach
  public void setUp() {
    this.reservationRepository = Mockito.mock(ReservationRepository.class);
    this.workspaceRepository = Mockito.mock(WorkspaceRepository.class);
    this.workspaceService = spy(new WorkspaceService(workspaceRepository, reservationRepository));
  }

  @Test
  public void givenWorkspaceDto_whenAddWorkspace_thenReturnSuccessMessage() {
    WorkspaceDto workspaceDto = workspaceDto();
    Workspace workspace = new Workspace(workspaceDto);

    doNothing().when(workspaceRepository).save(workspace);

    Result<String> result = workspaceService.addWorkspace(workspaceDto);

    String expectedMessage = "Workspace is added";
    String actualMessage = result.getResultValue();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void whenGetWorkspaces_thenReturnListOfWorkspaces() {
    when(workspaceRepository.findAll())
        .thenReturn(List.of(workspace()));

    Result<List<Workspace>> result = workspaceService.getWorkspaces();

    Assertions.assertNotNull(result.getResultValue());
    Assertions.assertEquals(result.getResultValue().get(0), workspace());
  }

  @Test
  public void givenWorkspaceId_whenRemoveWorkspaceById_thenReturnSuccess() {
    Long workspaceId = 1L;

    when(workspaceRepository.findById(workspaceId))
        .thenReturn(Optional.of(workspace()));

    doNothing().when(workspaceRepository).delete(workspaceId);

    Result<String> result = workspaceService.removeWorkspaceById(workspaceId);

    String expectedMessage = "Workspace is deleted";
    String actualMessage = result.getResultValue();
    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void givenInvalidWorkspaceId_whenRemoveWorkspaceById_thenReturnWorkspaceNotFoundError() {
    when(workspaceRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    Result<String> result = workspaceService.removeWorkspaceById(anyLong());

    String expectedMessage = "Workspace not found";
    String actualMessage = result.getErrorMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void givenPeriodDto_whenGetAvailableWorkspaces_thenReturnAvailableWorkspaces() {
    PeriodDto periodDto = periodDto();

    when(workspaceRepository.findAll())
        .thenReturn(List.of(workspace()));

    when(reservationRepository.getReservationByWorkspaceIdAndPeriod(workspace().getId(), periodDto))
        .thenReturn(Collections.EMPTY_LIST);

    Result<List<Workspace>> result = workspaceService.getAvailableWorkspaces(periodDto);

    Assertions.assertNotNull(result.getResultValue());
    Assertions.assertEquals(result.getResultValue().get(0), workspace());
  }

  private static WorkspaceDto workspaceDto() {
    return new WorkspaceDto(
      "test_w",
        WorkspaceType.OPEN_SPACE,
        BigDecimal.valueOf(100)
    );
  }

  private static Workspace workspace() {
    return Workspace.builder()
        .id(1L)
        .name("test_w")
        .type(WorkspaceType.OPEN_SPACE)
        .price(BigDecimal.valueOf(100.0))
        .build();
  }

  private static PeriodDto periodDto() {
    return PeriodDto.parseStr("2024-01-01", "2024-01-02");
  }
}
