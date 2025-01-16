package by.andersen.dto;

import by.andersen.enums.WorkspaceType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceDto {
  private String name;
  private WorkspaceType type;
  private BigDecimal price;
}
