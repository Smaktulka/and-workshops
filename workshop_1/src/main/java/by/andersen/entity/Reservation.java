package by.andersen.entity;

import by.andersen.dto.PeriodDto;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder
public class Reservation implements Identifiable<Long>, Serializable {
  private Long id;
  private Long ownerId;
  private Long workspaceId;
  private LocalDate startTime;
  private LocalDate endTime;

  @Override
  public Long getId() {
    return id;
  }

  public PeriodDto getPeriod() {
    return new PeriodDto(startTime, endTime);
  }
}
