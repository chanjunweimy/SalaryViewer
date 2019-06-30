package cdit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import cdit.exception.CditException;
import cdit.exception.InvalidCsvException;
import cdit.exception.UserDuplicateException;
import cdit.exception.UserListValidationException;
import cdit.exception.UserMissingCsvHeaderException;
import cdit.exception.UserNameIsEmptyException;
import cdit.exception.UserSalaryInvalidTypeException;
import cdit.exception.UserSalaryOutOfRangeException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
  public static final String MSG_INVALID_CSV = "The CSV file is invalid. Please revise the file.";
  public static final String MSG_USER_MISSING_HEADER =
      "Please revise your Users\' CSV file as the headers are missing.";
  public static final String MSG_USER_NAME_EMPTY =
      "Please revise your Users\' CSV file as the \'name\' should not be empty.";
  public static final String MSG_USER_NAME_DUPLICATE =
      "Please revise your Users\' CSV file as it contains multiple users with same \'name\'.";
  public static final String MSG_USER_SALARY_INVALID =
      "Please revise your Users\' CSV file as the \'salary\' must be a number between 0 to 4000.";
  public static final String MSG_USER_CSV_INVALID =
      "Please revise your Users\' CSV file as it fails the validation.";
  public static final String MSG_EXCEPTION =
      "An error has occured. Please contact chanjunweimy@gmail.com for more details.";

  private Logger _logger = LoggerFactory.getLogger(RestExceptionHandler.class);


  @ExceptionHandler({InvalidCsvException.class})
  protected ResponseEntity<Object> handleInvalidCsvException(Exception ex, WebRequest request) {
    return handleExceptionInternal(ex, MSG_INVALID_CSV, new HttpHeaders(), HttpStatus.BAD_REQUEST,
        request);
  }

  @ExceptionHandler({UserMissingCsvHeaderException.class})
  protected ResponseEntity<Object> handleUserMissingCsvHeaderException(Exception ex,
      WebRequest request) {
    return handleExceptionInternal(ex, MSG_USER_MISSING_HEADER, new HttpHeaders(),
        HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserNameIsEmptyException.class})
  protected ResponseEntity<Object> handleUserNameIsEmptyException(Exception ex,
      WebRequest request) {
    return handleExceptionInternal(ex, MSG_USER_NAME_EMPTY, new HttpHeaders(),
        HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserDuplicateException.class})
  protected ResponseEntity<Object> handleUserDuplicateException(Exception ex, WebRequest request) {
    return handleExceptionInternal(ex, MSG_USER_NAME_DUPLICATE, new HttpHeaders(),
        HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserSalaryInvalidTypeException.class})
  protected ResponseEntity<Object> handleUserSalaryInvalidTypeException(Exception ex,
      WebRequest request) {
    return handleExceptionInternal(ex, MSG_USER_SALARY_INVALID, new HttpHeaders(),
        HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserSalaryOutOfRangeException.class})
  protected ResponseEntity<Object> handleUserSalaryOutOfRangeException(Exception ex,
      WebRequest request) {
    return handleExceptionInternal(ex, MSG_USER_SALARY_INVALID, new HttpHeaders(),
        HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserListValidationException.class})
  protected ResponseEntity<Object> handleUserListValidationException(Exception ex,
      WebRequest request) {
    _logger.error(ex.getMessage());
    return handleExceptionInternal(ex, MSG_USER_CSV_INVALID, new HttpHeaders(),
        HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({CditException.class})
  protected ResponseEntity<Object> handleCditException(Exception ex, WebRequest request) {
    _logger.error(ex.getMessage());
    return handleExceptionInternal(ex, MSG_EXCEPTION, new HttpHeaders(), HttpStatus.BAD_REQUEST,
        request);
  }

  @ExceptionHandler({Exception.class})
  protected ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
    _logger.error(ex.getMessage());
    return handleExceptionInternal(ex, MSG_EXCEPTION, new HttpHeaders(), HttpStatus.BAD_REQUEST,
        request);
  }
}
