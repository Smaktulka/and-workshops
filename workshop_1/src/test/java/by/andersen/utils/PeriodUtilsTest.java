package by.andersen.utils;

import by.andersen.dto.PeriodDto;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class PeriodUtilsTest {
  @ParameterizedTest
  @MethodSource("provideOverlappingPeriods")
  public void givenOverlappingFirstPeriodAndSecondPeriod_whenPeriodsOverlap_thenReturnTrue(
      PeriodDto overlappingFirstPeriod,
      PeriodDto overlappingSecondPeriod
  ) {
    boolean overlap = PeriodUtils.periodsOverlap(overlappingFirstPeriod, overlappingSecondPeriod);

    Assertions.assertTrue(overlap);
  }

  @ParameterizedTest
  @MethodSource("provideNotOverlappingPeriods")
  public void givenNotOverlappingFirstPeriodAndSecondPeriod_whenPeriodsOverlap_thenReturnFalse(
      PeriodDto notOverlappingFirstPeriod,
      PeriodDto notOverlappingSecondPeriod
  ) {
    boolean overlap = PeriodUtils.periodsOverlap(notOverlappingFirstPeriod, notOverlappingSecondPeriod);

    Assertions.assertFalse(overlap);
  }

  private static Stream<Arguments> provideOverlappingPeriods() {
    return Stream.of(
        getLeftOverlappingPeriods(),
        getRightOverlappingPeriods(),
        getEqualPeriods(),
        getInnerOverlappingPeriods()
    );
  }

  private static Stream<Arguments> provideNotOverlappingPeriods() {
    return Stream.of(
        Arguments.of(PeriodDto.parseStr("2024-01-01", "2024-01-03"), PeriodDto.parseStr("2024-02-01", "2024-02-03")),
        Arguments.of(PeriodDto.parseStr("2025-01-01", "2025-01-03"), PeriodDto.parseStr("2026-02-01", "2026-02-03")),
        Arguments.of(PeriodDto.parseStr("2024-01-01", "2024-01-03"), PeriodDto.parseStr("2024-01-03", "2024-01-06"))
    );
  }


  private static Arguments getLeftOverlappingPeriods() {
    return Arguments.of(
        PeriodDto.parseStr("2024-01-10", "2024-01-15"), PeriodDto.parseStr("2024-01-12", "2024-01-17")
    );
  }

  private static Arguments getRightOverlappingPeriods() {
    return Arguments.of(
        PeriodDto.parseStr("2024-01-15", "2024-01-20"), PeriodDto.parseStr("2024-01-12", "2024-01-17")
    );
  }

  private static Arguments getEqualPeriods() {
    return Arguments.of(
        PeriodDto.parseStr("2024-01-12", "2024-01-17"), PeriodDto.parseStr("2024-01-12", "2024-01-17")
    );
  }

  private static Arguments getInnerOverlappingPeriods() {
    return Arguments.of(
        PeriodDto.parseStr("2024-01-12", "2024-01-17"), PeriodDto.parseStr("2024-01-13", "2024-01-16")
    );
  }

}
