package by.andersen.coworkingspace.entity;

import by.andersen.coworkingspace.enums.WorkspaceType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Table(name = "workspace", schema = "space")
@Entity
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder
public class Workspace {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "workspace_type")
  private WorkspaceType type;
  private BigDecimal price;

  @OneToMany(mappedBy = "workspace", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Reservation> reservations;
}
