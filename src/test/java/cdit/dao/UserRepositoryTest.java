package cdit.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import cdit.model.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@org.springframework.transaction.annotation.Transactional()
public class UserRepositoryTest {
  @Autowired private DataSource dataSource;
  @Autowired private JdbcTemplate jdbcTemplate;
  @Autowired private EntityManager entityManager;
  @Autowired private UserRepository userRepository;

  @Test
  void testInjectedComponentsAreNotNull(){
    assertNotNull(dataSource);
    assertNotNull(jdbcTemplate);
    assertNotNull(entityManager);
    assertNotNull(userRepository);
  }
  
  @Test
  void testBasicOperations() {
    User expectedUser = new User("alice", 1);
    userRepository.saveAndFlush(expectedUser);
    Iterable<User> users = userRepository.findAll();
    assertNotNull(users);
    for (User user : users) {
      assertEquals(expectedUser.getName(), user.getName());
      assertEquals(expectedUser.getSalary(), user.getSalary());
    }
    userRepository.deleteAllInBatch();
    users = userRepository.findAll();
    for (User user : users) {
      fail();
      assertNull(user);
    }
  }
  
  @Test
  void testSamePrimaryKeyIsUpdate() {
    String name = "alice";
    User expectedUser1 = new User(name, 1);
    User expectedUser2 = new User(name, 2);
    userRepository.saveAndFlush(expectedUser1);
    userRepository.saveAndFlush(expectedUser2);
    Optional<User> userOptional = userRepository.findById(name);
    assertTrue(userOptional.isPresent());
    User user = userOptional.get();
    assertEquals(expectedUser2.getName(), user.getName());
    assertEquals(expectedUser2.getSalary(), user.getSalary());
  }
  
  @Test  
  void testSalaryConstraint_MinFailed() {
    String name = "alice";
    User expectedUser = new User(name, -0.1);
    assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(expectedUser));
  } 
  
  @Test  
  void testSalaryConstraint_MaxFailed() {
    String name = "alice";
    User expectedUser = new User(name, 4000.1);
    assertThrows(ConstraintViolationException.class, () -> userRepository.saveAndFlush(expectedUser));
  }
  
  @Test  
  void testSalaryConstraint_MinSuccess() {
    String name = "alice";
    User expectedUser = new User(name, 0);
    userRepository.saveAndFlush(expectedUser);
    Optional<User> userOptional = userRepository.findById(name);
    assertTrue(userOptional.isPresent());
    User user = userOptional.get();  
    assertEquals(expectedUser.getName(), user.getName());
    assertEquals(expectedUser.getSalary(), user.getSalary());
  }
  
  @Test  
  void testSalaryConstraint_MaxSuccess() {
    String name = "alice";
    User expectedUser = new User(name, 0);
    userRepository.saveAndFlush(expectedUser);
    Optional<User> userOptional = userRepository.findById(name);
    assertTrue(userOptional.isPresent());
    User user = userOptional.get();  
    assertEquals(expectedUser.getName(), user.getName());
    assertEquals(expectedUser.getSalary(), user.getSalary());
  }
}
