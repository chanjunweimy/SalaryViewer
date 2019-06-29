package cdit.service;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Service;
import cdit.dao.UserRepository;
import cdit.model.User;

@Service()
public class UserServiceImpl implements UserService {
  private final UserRepository _userRepository;
  private final ReentrantLock _lock = new ReentrantLock();

  public UserServiceImpl(UserRepository userRepository) {
    _userRepository = userRepository;
  }

  @Override
  public void updateUsers(List<User> users) {
    _lock.lock();
    try {
      _userRepository.deleteAll();
      _userRepository.saveAll(users);
      _userRepository.flush();
    } finally {
      _lock.unlock();
    }
  }

  @Override
  public List<User> getAllUsers() {
    _lock.lock();
    List<User> users = null;
    try {
      users = _userRepository.findAll();
    } finally {
      _lock.unlock();
    }
    return users;
  }
}
