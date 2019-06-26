package cdit.service;

import java.util.List;
import cdit.model.User;

public interface UserService {
  public void updateUsers(List<User> users);
  public List<User> getAllUsers();
}
