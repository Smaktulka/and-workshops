package by.andersen.coworkingspace.dto;

import java.time.DateTimeException;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PeriodDto {
  private LocalDate startDate;
  private LocalDate endDate;

  public static final String DATE_PATTERN = "yyyy-MM-dd";

  public PeriodDto(LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;

    if (!isValid()) {
      throw new DateTimeException("Date is invalid! (start date is after end date)");
    }
  }

  private boolean isValid() {
    return startDate.isBefore(endDate) || startDate.equals(endDate);
  }
}
