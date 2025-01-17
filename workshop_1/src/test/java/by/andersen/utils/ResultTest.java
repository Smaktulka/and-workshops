package by.andersen.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ResultTest {
  @Test
  public void givenErrorMessage_whenError_thenReturnResultWithEmptyResultValue() {
    Result<String> result = Result.error("error");

    Assertions.assertTrue(result.isEmpty());
    Assertions.assertNull(result.getResultValue());
  }

  @Test
  public void givenResultValue_whenOk_thenReturnResultWithEmptyErrorMessage() {
    Result<String> result = Result.ok("result");

    Assertions.assertFalse(result.isEmpty());
    Assertions.assertNull(result.getErrorMessage());
  }
}
