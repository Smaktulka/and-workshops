package by.andersen.coworkingspace.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "token", schema = "space")
public class RefreshToken {
  @Id
  @GeneratedValue
  private Long id;
  private String refreshToken;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;
}
