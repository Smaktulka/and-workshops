package by.andersen.utils;

import by.andersen.dto.PeriodDto;
import by.andersen.dto.ReservationDto;
import by.andersen.dto.WorkspaceDto;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;

public class DtoScannerTest {
  private final InputStream standardIn = System.in;

  @AfterEach
  public void tearDown() {
    System.setIn(standardIn);
  }

  @ParameterizedTest
  @MethodSource("provideDtoClassesAndValidConsoleInputs")
  public <T> void givenDtoClassAndValidConsoleInput_whenScan_thenReturnOptionalOfDtoInstance(Class<T> dtoClass, String input) {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes());
    System.setIn(inputStream);

    Optional<T> dto = DtoScanner.scan(dtoClass);

    Assertions.assertTrue(dto.isPresent());
    Assertions.assertEquals(dto.get().getClass(), dtoClass);
  }

  @Test
  public void givenNull_whenScan_thenThrowRuntimeException() {
    Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> DtoScanner.scan(null));

    String expectedMessage = "Dto class cannot be null";
    String actualMessage = e.getMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  private static Stream<Arguments> provideDtoClassesAndValidConsoleInputs() {
    return Stream.of(
        getPeriodDtoClassAndValidConsoleInput(),
        getReservationDtoAndValidConsoleInput(),
        getWorkspaceDtoAndValidConsoleInput()
    );
  }

  private static Arguments getPeriodDtoClassAndValidConsoleInput() {
    return Arguments.of(PeriodDto.class, getValidPeriodDtoConsoleInput());
  }

  private static String getValidPeriodDtoConsoleInput() {
    return "2025-01-01\n2025-01-02";
  }

  private static String getInvalidPeriodDtoConsoleInput() {
    return "1234.22.22\n1234.22.22";
  }

  private static Arguments getReservationDtoAndValidConsoleInput() {
    return Arguments.of(ReservationDto.class, getValidReservationDtoConsoleInput());
  }

  private static String getValidReservationDtoConsoleInput() {
    return String.format("%s\n%s", Mockito.anyLong(), getValidPeriodDtoConsoleInput());
  }

  private static Arguments getWorkspaceDtoAndValidConsoleInput() {
    return Arguments.of(WorkspaceDto.class, getValidWorkspaceDtoConsoleInput());
  }

  private static String getValidWorkspaceDtoConsoleInput() {
    return String.format(
        "%s\nopen_space\n%s",
        Mockito.anyString(),
        Mockito.anyDouble()
    );
  }
}
