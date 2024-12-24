package by.andersen.exception;

public class DtoScanException extends RuntimeException {
  public DtoScanException(String errorMessage, Exception e) {
    super(errorMessage, e);
  }

  public DtoScanException(Exception e) {
    super(e);
  }
}
