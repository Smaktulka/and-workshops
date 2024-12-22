package by.andersen.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PeriodDto {
  private LocalDate startDate;
  private LocalDate endDate;

  public boolean isValid() {
    return startDate.isBefore(endDate);
  }
}
