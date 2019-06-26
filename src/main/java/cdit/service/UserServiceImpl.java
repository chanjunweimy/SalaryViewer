package cdit.service;

import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.stereotype.Service;
import cdit.dao.UserRepository;
import cdit.model.User;

@Service()
public class UserServiceImpl implements UserService {
  private final UserRepository _userRepository;
  private ReadWriteLock _lock;
  
  public UserServiceImpl(UserRepository userRepository) {
    _userRepository = userRepository;
    _lock = new ReentrantReadWriteLock();
  }

  @Override
  public void updateUsers(List<User> users) {
    _lock.writeLock().lock();
    try {
      _userRepository.deleteAll();
      _userRepository.saveAll(users);  
      _userRepository.flush();
    } finally {
      _lock.writeLock().unlock();
    }    
  }

  @Override
  public List<User> getAllUsers() {
    _lock.readLock().lock();
    List<User> users = null;
    try {
      users = _userRepository.findAll();
    } finally {
      _lock.readLock().unlock();
    }    
    return users;
  }
}
