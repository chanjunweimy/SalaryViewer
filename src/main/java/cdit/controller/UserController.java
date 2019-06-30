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
import cdit.exception.CditException;
import cdit.model.User;

@RestController
public class UserController {
  private final CsvParserService _csvParserService;
  private final UserMapperService _userMapperService;
  private final UserService _userService;

  private static final String ENDPOINT_USERS = "/users";

  private Logger _logger = LoggerFactory.getLogger(UserController.class);

  public UserController(CsvParserService csvParserService, UserMapperService userMapperService,
      UserService userService) {
    _csvParserService = csvParserService;
    _userMapperService = userMapperService;
    _userService = userService;
  }

  @PostMapping(value = ENDPOINT_USERS)
  public ResponseEntity<?> updateUsers(@RequestParam("file") MultipartFile multipartFile)
      throws CditException, IOException {
    LoggerHelper.logMessageAtStartOfMethod(_logger, LoggerHelper.METHOD_POST, ENDPOINT_USERS,
        "UserController");

    List<User> users =
        _csvParserService.parseInputStream(multipartFile.getInputStream(), _userMapperService);
    _userService.updateUsers(users);

    LoggerHelper.logMessageAtEndOfMethod(_logger, LoggerHelper.METHOD_POST, ENDPOINT_USERS,
        "UserController");

    return ResponseEntity.ok().build();
  }

  @GetMapping(value = ENDPOINT_USERS)
  public List<User> getUsers() {
    LoggerHelper.logMessageAtStartOfMethod(_logger, LoggerHelper.METHOD_GET, ENDPOINT_USERS,
        "UserController");

    List<User> users = _userService.getAllUsers();

    LoggerHelper.logMessageAtEndOfMethod(_logger, LoggerHelper.METHOD_POST, ENDPOINT_USERS,
        "UserController");
    return users;
  }
}
