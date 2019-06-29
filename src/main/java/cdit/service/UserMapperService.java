package cdit.service;

import java.util.List;
import cdit.exception.UserListValidationException;
import cdit.model.User;

public interface UserMapperService {
  public List<User> mapStringArraysToUsers(List<String[]> stringArrays)
      throws UserListValidationException;
}
