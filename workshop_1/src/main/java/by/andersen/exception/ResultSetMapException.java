package by.andersen.exception;

public class ResultSetMapException extends RuntimeException {
  public ResultSetMapException(String errorMessage, Exception e) {
    super(errorMessage, e);
  }
}
