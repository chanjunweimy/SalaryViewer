package cdit;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import cdit.exception.InvalidCsvException;
import cdit.exception.UserDuplicateException;
import cdit.exception.UserListValidationException;
import cdit.exception.UserMissingCsvHeaderException;
import cdit.exception.UserNameIsEmptyException;
import cdit.exception.UserSalaryInvalidTypeException;
import cdit.exception.UserSalaryOutOfRangeException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({InvalidCsvException.class})
  protected ResponseEntity<Object> handleInvalidCsvException(Exception ex, WebRequest request) {
    return handleExceptionInternal(ex, "The CSV file is invalid. Please revise the file.",
        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserMissingCsvHeaderException.class})
  protected ResponseEntity<Object> handleUserMissingCsvHeaderException(Exception ex,
      WebRequest request) {
    return handleExceptionInternal(ex,
        "Please revise your Users\' CSV file as the headers are missing.", new HttpHeaders(),
        HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserNameIsEmptyException.class})
  protected ResponseEntity<Object> handleUserNameIsEmptyException(Exception ex,
      WebRequest request) {
    return handleExceptionInternal(ex,
        "Please revise your Users\' CSV file as the \'name\' should not be empty.",
        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserDuplicateException.class})
  protected ResponseEntity<Object> handleUserDuplicateException(Exception ex, WebRequest request) {
    return handleExceptionInternal(ex,
        "Please revise your Users\' CSV file as it contains multiple users with same \'name\'.",
        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserSalaryInvalidTypeException.class})
  protected ResponseEntity<Object> handleUserSalaryInvalidTypeException(Exception ex,
      WebRequest request) {
    return handleExceptionInternal(ex,
        "Please revise your Users\' CSV file as the \'salary\' must be a number between 0 to 4000.",
        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserSalaryOutOfRangeException.class})
  protected ResponseEntity<Object> handleUserSalaryOutOfRangeException(Exception ex,
      WebRequest request) {
    return handleExceptionInternal(ex,
        "Please revise your Users\' CSV file as the \'salary\' must be a number between 0 to 4000.",
        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({UserListValidationException.class})
  protected ResponseEntity<Object> handleUserListValidationException(Exception ex,
      WebRequest request) {
    return handleExceptionInternal(ex,
        "Please revise your Users\' CSV file as it fails the validation.", new HttpHeaders(),
        HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler({Exception.class})
  protected ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
    return handleExceptionInternal(ex,
        "An error has occured. Please contact chanjunweimy@gmail.com for more details.",
        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }
}
