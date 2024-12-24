package by.andersen.entity;

import by.andersen.dto.WorkspaceDto;
import by.andersen.enums.WorkspaceType;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder
public class Workspace implements Identifiable<Long>, Serializable {
  private Long id;
  private String name;
  private WorkspaceType type;
  private BigDecimal price;

  @Override
  public Long getId() {
    return id;
  }

  public Workspace(Long id, WorkspaceDto workspaceDto) {
    this.id = id;
    this.name = workspaceDto.getName();
    this.type = workspaceDto.getType();
    this.price = workspaceDto.getPrice();
  }
}
