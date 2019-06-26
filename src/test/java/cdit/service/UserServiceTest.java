package cdit.service;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import cdit.SwaggerConfig;
import cdit.dao.UserRepository;
import cdit.model.User;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
@ComponentScan(basePackages = "cdit", excludeFilters=@Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SwaggerConfig.class))
@org.springframework.transaction.annotation.Transactional()
public class UserServiceTest {  
  private static final double EPSILON = 0.001;
  
  @Autowired private UserRepository _userRepository;
  @Autowired private UserServiceImpl _userService;  
  
  @Test
  public void testInjectedComponentsAreNotNull(){
    assertNotNull(_userRepository);
    assertNotNull(_userService);
  }
  
  @Test
  public void testGetAllUsers(){
    User expectedUser = new User("alice", 1);
    _userRepository.saveAndFlush(expectedUser);
    List<User> actualUsers = _userService.getAllUsers();
    assertEquals(1, actualUsers.size());
    User actualUser = actualUsers.get(0);
    assertEquals(expectedUser.getName(), actualUser.getName());
    assertEquals(expectedUser.getSalary(), actualUser.getSalary(), EPSILON);
  }
  
  @Test
  public void testUpdateUsers_AddNew(){
    assertEquals(0, _userRepository.findAll().size());   

    User expectedUser = new User("alice", 1);
    List<User> expectedUsers = new ArrayList<User>();
    expectedUsers.add(expectedUser);
    
    _userService.updateUsers(expectedUsers);

    List<User> actualUsers = _userRepository.findAll();
    assertEquals(1, actualUsers.size());
    User actualUser = actualUsers.get(0);
    assertEquals(expectedUser.getName(), actualUser.getName());
    assertEquals(expectedUser.getSalary(), actualUser.getSalary(), EPSILON);
  }
  
  @Test
  public void testUpdateUsers_ReplaceExisting(){
    User originalUser = new User("Bob", 2);
    _userRepository.saveAndFlush(originalUser);

    assertEquals(1, _userRepository.findAll().size());   

    User expectedUser = new User("alice", 1);
    List<User> expectedUsers = new ArrayList<User>();
    expectedUsers.add(expectedUser);
    
    _userService.updateUsers(expectedUsers);

    List<User> actualUsers = _userRepository.findAll();
    assertEquals(1, actualUsers.size());
    User actualUser = actualUsers.get(0);
    assertEquals(expectedUser.getName(), actualUser.getName());
    assertEquals(expectedUser.getSalary(), actualUser.getSalary(), EPSILON);
  }
}
