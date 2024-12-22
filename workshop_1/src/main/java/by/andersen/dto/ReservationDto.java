package by.andersen.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationDto {
  private Long workspaceId;
  private LocalDate startTime;
  private LocalDate endTime;

  public PeriodDto getPeriod() {
    return new PeriodDto(startTime, endTime);
  }
}
