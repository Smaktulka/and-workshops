package by.andersen.coworkingspace.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreRemove;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservation", schema = "space")
public class Reservation{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "owner_id")
  @JsonIgnore
  private User owner;

  @ManyToOne(cascade = CascadeType.MERGE)
  @JoinColumn(name = "workspace_id")
  @JsonIgnore
  private Workspace workspace;

  private LocalDate startTime;
  private LocalDate endTime;

  @PreRemove
  private void removeReservationFromOwnerAndWorkspace() {
    if (owner != null) {
      owner.getReservations().remove(this);
    }

    if (workspace != null) {
      workspace.getReservations().remove(this);
    }
  }
}
