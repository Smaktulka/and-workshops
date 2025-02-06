package by.andersen.coworkingspace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokensDto {
  private String accessToken;
  private String refreshToken;
}
