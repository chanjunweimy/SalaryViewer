package cdit.controller;

import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import cdit.service.CsvParserService;
import cdit.service.UserMapperService;
import cdit.service.UserService;
import cdit.exception.InvalidCsvException;
import cdit.exception.UserListValidationException;
import cdit.model.User;

@RestController
public class UserController {
  private final CsvParserService _csvParserService;
  private final UserMapperService _userMapperService;
  private final UserService _userService;

  public UserController(CsvParserService csvParserService, UserMapperService userMapperService, UserService userService) {
    _csvParserService = csvParserService;
    _userMapperService = userMapperService;
    _userService = userService;
  }
  
  @PostMapping(value = "/users")
  public void UpdateUsers(@RequestParam("file") MultipartFile multipartFile) {
    try {
      List<String[]> stringArrays = _csvParserService.loadStringArrays(multipartFile.getInputStream());
      List<User> users = _userMapperService.mapStringArraysToUsers(stringArrays);
      _userService.updateUsers(users);
    } catch (InvalidCsvException | IOException | UserListValidationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  @GetMapping(value = "/users")
  public List<User> GetUsers() {
    return _userService.getAllUsers();
  }
}
