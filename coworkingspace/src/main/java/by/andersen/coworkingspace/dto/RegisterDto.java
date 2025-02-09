package by.andersen.coworkingspace.dto;

import by.andersen.coworkingspace.enums.UserRole;
import lombok.Data;

@Data
public class RegisterDto {
  private String userName;
  private UserRole role;
  private char[] password;
}
