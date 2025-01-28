package by.andersen.entity;

import by.andersen.dto.WorkspaceDto;
import by.andersen.enums.WorkspaceType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;
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
public class Workspace implements Identifiable<Long>, Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private WorkspaceType type;
  private BigDecimal price;

  @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Reservation> reservations;

  @Override
  public Long getId() {
    return id;
  }

  public Workspace(WorkspaceDto workspaceDto) {
    this.id = 0L;
    this.name = workspaceDto.getName();
    this.type = workspaceDto.getType();
    this.price = workspaceDto.getPrice();
  }

  public Workspace(Long id) {
    this.id = id;
  }
}
