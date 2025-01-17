package by.andersen.dto;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class PeriodDto {
  private final LocalDate startDate;
  private final LocalDate endDate;

  public static final String DATE_PATTERN = "yyyy-MM-dd";

  public PeriodDto(LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;

    if (!isValid()) {
      throw new DateTimeException("Date is invalid! (start date is after end date)");
    }
  }

  public static PeriodDto parseStr(String startDate, String endDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PeriodDto.DATE_PATTERN);

    return new PeriodDto(
        LocalDate.parse(startDate, formatter),
        LocalDate.parse(endDate, formatter)
    );
  }

  private boolean isValid() {
    return startDate.isBefore(endDate) || startDate.equals(endDate);
  }
}
