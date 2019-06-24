package cdit.controller;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();

  @RequestMapping(value = "/users", method = RequestMethod.GET)
  public void GetUsers() {

  }

  @RequestMapping(value = "/users", method = RequestMethod.POST)
  public void UpdateUsers() {

  }
}
