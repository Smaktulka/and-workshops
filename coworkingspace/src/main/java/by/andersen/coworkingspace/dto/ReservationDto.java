package by.andersen.coworkingspace.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReservationDto{
  private Long workspaceId;
  private LocalDate startTime;
  private LocalDate endTime;
  public PeriodDto getPeriod() {
    return new PeriodDto(startTime, endTime);
  }
}
