package cdit.controller;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import cdit.service.UserService;
import cdit.model.User;

@RestController
public class UserController {
  private final UserService _userService;
  
  public UserController(UserService userService) {
    _userService = userService;
  }
  
  @RequestMapping(value = "/users", method = RequestMethod.GET)
  public List<User> GetUsers() {
    return _userService.getAllUsers();
  }

  @RequestMapping(value = "/users", method = RequestMethod.POST)
  public void UpdateUsers() {

  }
}
