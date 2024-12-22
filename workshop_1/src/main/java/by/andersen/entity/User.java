package by.andersen.entity;

import by.andersen.enums.UserRole;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@Builder
public class User implements Identifiable<Long> {
  private Long id;
  private String name;
  private UserRole role;
  private String passwordHash;

  @Override
  public Long getId() {
    return id;
  }
}
