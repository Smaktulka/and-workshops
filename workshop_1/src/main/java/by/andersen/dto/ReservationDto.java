package by.andersen.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationDto {
  private Long workspaceId;
  private LocalDate startTime;
  private LocalDate endTime;

  public PeriodDto getPeriod() {
    return new PeriodDto(startTime, endTime);
  }
}
