package by.andersen.coworkingspace.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class PeriodDto {
  public static final String DATE_PATTERN = "yyyy-MM-dd";

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
  private LocalDate startTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_PATTERN)
  private LocalDate endTime;


  public PeriodDto(LocalDate startTime, LocalDate endTime) {
    this.startTime = startTime;
    this.endTime = endTime;

    if (!isValid()) {
      throw new DateTimeException("Date is invalid! (start date is after end date)");
    }
  }

  private boolean isValid() {
    return startTime.isBefore(endTime) || startTime.equals(endTime);
  }
}
