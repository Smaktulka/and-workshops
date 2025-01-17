package by.andersen.dto;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PeriodDtoTest {
  @ParameterizedTest
  @MethodSource("provideValidStartDateAndEndDate")
  public void givenValidStartDateAndEndDate_whenCallingPeriodDtoAllArgsConstructor_thenReturnValidPeriod(
      LocalDate validStartDate,
      LocalDate validEndDate
  ) {
    PeriodDto periodDto = new PeriodDto(validStartDate, validEndDate);

    Assertions.assertTrue(periodDto.getStartDate().isBefore(periodDto.getEndDate()));
  }

  @ParameterizedTest
  @MethodSource("provideInvalidStartDateAndEndDate")
  public void givenInvalidStartDateAndEndDate_whenPeriodDtoAllArgsConstructor_thenThrowDateTimeException(
      LocalDate invalidStartDate,
      LocalDate invalidEndDate
  ) {
    Exception e = Assertions.assertThrows(
        DateTimeException.class,
        () -> new PeriodDto(invalidStartDate, invalidEndDate)
    );

    String expectedMessage = "Date is invalid! (start date is after end date)";
    String actualMessage = e.getMessage();

    Assertions.assertEquals(expectedMessage, actualMessage);
  }

  private static Stream<Arguments> provideValidStartDateAndEndDate() {
    return Stream.of(
        Arguments.of(parseStrToDate("2024-01-01"), parseStrToDate("2024-01-02")),
        Arguments.of(parseStrToDate("2025-01-01"), parseStrToDate("2026-01-02")),
        Arguments.of(parseStrToDate("2024-04-12"), parseStrToDate("2024-04-20"))
    );
  }

  private static Stream<Arguments> provideInvalidStartDateAndEndDate() {
    return Stream.of(
        Arguments.of(parseStrToDate("2024-01-02"), parseStrToDate("2024-01-01")),
        Arguments.of(parseStrToDate("2026-01-01"), parseStrToDate("2024-01-02")),
        Arguments.of(parseStrToDate("2024-04-20"), parseStrToDate("2024-04-02"))
    );
  }

  private static LocalDate parseStrToDate(String strDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PeriodDto.DATE_PATTERN);
    return LocalDate.parse(strDate, formatter);
  }
}
