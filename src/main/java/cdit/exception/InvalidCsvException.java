package cdit.exception;

public class InvalidCsvException extends CditException {
  /**
   * 
   */
  private static final long serialVersionUID = -6269600736150550219L;
  
  public InvalidCsvException() {
    super();
  }

  public InvalidCsvException(String message) {
    super(message);
  } 
}
