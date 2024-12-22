package by.andersen.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Result<T> {
  private T resultValue;
  private String errorMessage;

  public boolean isEmpty() {
    return resultValue == null;
  }
  public static <T> Result<T> error(String errorMessage) {
    return new Result<>(null, errorMessage);
  }

  public static <T> Result<T> ok(T resultValue) {
    return new Result<>(resultValue, null);
  }
}
