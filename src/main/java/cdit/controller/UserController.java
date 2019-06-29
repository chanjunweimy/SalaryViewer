package cdit.controller;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import cdit.service.CsvParserService;
import cdit.service.UserMapperService;
import cdit.service.UserService;
import cdit.util.LoggerHelper;
import cdit.exception.InvalidCsvException;
import cdit.exception.UserListValidationException;
import cdit.model.User;

@RestController
public class UserController {
  private final CsvParserService _csvParserService;
  private final UserMapperService _userMapperService;
  private final UserService _userService;
  
  private static final String ENDPOINT_USERS = "/users";
  
  Logger _logger = LoggerFactory.getLogger(UserController.class);

  public UserController(CsvParserService csvParserService, UserMapperService userMapperService, UserService userService) {
    _csvParserService = csvParserService;
    _userMapperService = userMapperService;
    _userService = userService;
  }
  
  @PostMapping(value = ENDPOINT_USERS)
  public ResponseEntity<?> UpdateUsers(@RequestParam("file") MultipartFile multipartFile) throws InvalidCsvException, UserListValidationException, IOException {
    LoggerHelper.LogMessageAtStartOfMethod(_logger, LoggerHelper.METHOD_POST, ENDPOINT_USERS, "UserController");
    
    List<String[]> stringArrays = _csvParserService.loadStringArrays(multipartFile.getInputStream());
    List<User> users = _userMapperService.mapStringArraysToUsers(stringArrays);
    _userService.updateUsers(users);
    
    LoggerHelper.LogMessageAtEndOfMethod(_logger, LoggerHelper.METHOD_POST, ENDPOINT_USERS, "UserController");

    return ResponseEntity.ok().build();
    /*
    try {
      
    } catch (InvalidCsvException e) {
      return ResponseEntity.badRequest().body(""); 
    } catch (UserListValidationException e) {
      return ResponseEntity.badRequest().body(""); 
    } catch (Exception e) {
      return ResponseEntity.badRequest().body("Failed to update users. Please contact the System Administrator for more details."); 
    }
    */
  }

  @GetMapping(value = ENDPOINT_USERS)
  public List<User> GetUsers() {
    LoggerHelper.LogMessageAtStartOfMethod(_logger, LoggerHelper.METHOD_GET, ENDPOINT_USERS, "UserController");
    
    List<User> users = _userService.getAllUsers();
    
    LoggerHelper.LogMessageAtEndOfMethod(_logger, LoggerHelper.METHOD_POST, ENDPOINT_USERS, "UserController");
    return users;
  }
}
