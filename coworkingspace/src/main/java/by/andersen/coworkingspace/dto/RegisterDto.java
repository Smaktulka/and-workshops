package by.andersen.coworkingspace.dto;

import by.andersen.coworkingspace.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {
  private String userName;
  private UserRole role;
  private char[] password;
}
