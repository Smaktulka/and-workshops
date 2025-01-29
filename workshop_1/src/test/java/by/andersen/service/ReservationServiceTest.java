package by.andersen.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import by.andersen.dto.ReservationDto;
import by.andersen.entity.Reservation;
import by.andersen.entity.User;
import by.andersen.entity.Workspace;
import by.andersen.enums.WorkspaceType;
import by.andersen.repository.ReservationRepository;
import by.andersen.repository.WorkspaceRepository;
import by.andersen.utils.Result;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class ReservationServiceTest {
  private ReservationService reservationService;
  @Mock
  private ReservationRepository reservationRepository;
  @Mock
  private WorkspaceRepository workspaceRepository;

  @BeforeEach
  public void setUp() {
    this.reservationRepository = Mockito.mock(ReservationRepository.class);
    this.workspaceRepository = Mockito.mock(WorkspaceRepository.class);
    this.reservationService = spy(new ReservationService(reservationRepository, workspaceRepository));
  }

  @Test
  public void whenGetReservations_thenReturnListOfReservations() {
    when(reservationRepository.findAll())
        .thenReturn(List.of(reservation()));

    Result<List<Reservation>> result = reservationService.getReservations();

    Assertions.assertFalse(result.getResultValue().isEmpty());
  }

  @Test
  public void givenUserId_whenGetUserReservation_thenReturnListOfReservations() {
    Long userId = 1L;
    when(reservationRepository.getReservationsByOwnerId(userId))
        .thenReturn(List.of(reservation()));

    Result<List<Reservation>> result = reservationService.getUserReservations(userId);

    Assertions.assertNotNull(result.getResultValue());
  }

  @Test
  public void givenUserIdAndReservationId_whenCancelReservation_thenReturnOkMessage() {
    Long userId = 1L;
    Long reservationId = 1L;

    when(reservationRepository.findById(reservationId))
        .thenReturn(Optional.of(reservation()));

    doNothing().when(reservationRepository).delete(userId);

    Result<String> result = reservationService.cancelReservation(userId, reservationId);

    String expectedMessage = "Reservation is cancelled";
    String actualMessage = result.getResultValue();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void givenInvalidReservationId_whenCancelReservation_thenReturnReservationNotFoundError() {
    Long userId = 1L;
    Long reservationId = 1L;

    when(reservationRepository.findById(reservationId))
        .thenReturn(Optional.empty());

    Result<String> result = reservationService.cancelReservation(userId, reservationId);

    String expectedMessage = "Cannot find reservation";
    String actualMessage = result.getErrorMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void givenInvalidUserId_whenCancelReservation_thenReturnPermissionDeniedError() {
    Long userId = 2L;
    Long reservationId = 1L;

    when(reservationRepository.findById(reservationId))
        .thenReturn(Optional.of(reservation()));

    Assertions.assertNotEquals(userId, reservation().getOwner().getId());

    Result<String> result = reservationService.cancelReservation(userId, reservationId);

    String expectedMessage = "Cannot cancel someone's else reservation";
    String actualMessage = result.getErrorMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void givenUserIdAndReservationDto_whenMakeReservation_thenReturnNewReservation() {
    Long userId = 1L;
    ReservationDto reservationDto = reservationDto();
    Workspace workspace = workspace();

    when(workspaceRepository.findById(reservationDto.getWorkspaceId()))
        .thenReturn(Optional.of(workspace));

    doReturn(true).when(reservationService)
        .isWorkspaceAvailable(userId, reservationDto.getPeriod());

    Result<Reservation> result = reservationService.makeReservation(userId, reservationDto);

    Assertions.assertNotNull(result.getResultValue());
  }

  @Test
  public void givenReservationDtoWithNonExistingWorkspaceId_whenMakeReservation_thenReturnWorkspaceNotFoundError() {
    Long userId = 1L;

    when(workspaceRepository.findById(anyLong()))
        .thenReturn(Optional.empty());

    Result<Reservation> result = reservationService.makeReservation(userId, reservationDto());

    String expectedMessage = "Workspace not found";
    String actualMessage = result.getErrorMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  public void givenUserIdAndReservationDto_whenMakeReservation_thenReturnReservationIsNotAvailableError() {
    Long userId = 1L;
    ReservationDto reservationDto = reservationDto();
    Workspace workspace = workspace();

    when(workspaceRepository.findById(reservationDto.getWorkspaceId()))
        .thenReturn(Optional.of(workspace));

    doReturn(false).when(reservationService)
        .isWorkspaceAvailable(userId, reservationDto.getPeriod());

    Result<Reservation> result = reservationService.makeReservation(userId, reservationDto);

    String expectedMessage = "Cannot create reservation on specified date";
    String actualMessage = result.getErrorMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  public static Reservation reservation() {
    return Reservation.builder()
        .id(1L)
        .startTime(LocalDate.parse("2024-01-01"))
        .endTime(LocalDate.parse("2024-01-02"))
        .owner(new User(1L))
        .workspace(new Workspace(1L))
        .build();
  }

  public static ReservationDto reservationDto() {
    return new ReservationDto(
        1L,
        LocalDate.parse("2024-01-01"),
        LocalDate.parse("2024-01-02")
    );
  }

  public static Workspace workspace() {
    return Workspace.builder()
        .id(1L)
        .name("test_w")
        .type(WorkspaceType.OPEN_SPACE)
        .price(BigDecimal.valueOf(100.0))
        .build();
  }
}
