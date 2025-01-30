package by.andersen.entity;

import by.andersen.dto.PeriodDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Entity
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder
public class Reservation implements Identifiable<Long>, Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "ownerId")
  private User owner;

  @ManyToOne
  @JoinColumn(name = "workspaceId")
  private Workspace workspace;

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
