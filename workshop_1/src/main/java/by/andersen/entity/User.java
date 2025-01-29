package by.andersen.entity;

import by.andersen.enums.UserRole;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
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
public class User implements Identifiable<Long>, Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;
  private UserRole role;
  private String passwordHash;

  @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private Set<Reservation> reservations;

  @Override
  public Long getId() {
    return id;
  }

  public User(Long id) {
    this.id = id;
  }
}
