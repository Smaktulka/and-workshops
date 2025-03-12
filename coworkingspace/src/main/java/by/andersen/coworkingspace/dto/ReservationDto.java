package by.andersen.coworkingspace.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ReservationDto {
  private Long workspaceId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PeriodDto.DATE_PATTERN)
  private LocalDate startTime;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PeriodDto.DATE_PATTERN)
  private LocalDate endTime;
}
