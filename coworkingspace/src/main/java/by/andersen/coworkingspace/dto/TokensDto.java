package by.andersen.coworkingspace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokensDto {
  private String accessToken;
  private String refreshToken;
}
