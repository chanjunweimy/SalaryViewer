package cdit.dao;

import static org.junit.Assert.*;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import javax.validation.ConstraintViolationException;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import cdit.SwaggerConfig;
import cdit.model.User;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@DataJpaTest
@ComponentScan(basePackages = "cdit",
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SwaggerConfig.class))
@org.springframework.transaction.annotation.Transactional()
public class UserRepositoryTest {
  private static final double EPSILON = 0.001;

  @Autowired
  private DataSource _dataSource;
  @Autowired
  private JdbcTemplate _jdbcTemplate;
  @Autowired
  private EntityManager _entityManager;
  @Autowired
  private UserRepository _userRepository;

  @Test
  public void testInjectedComponentsAreNotNull() {
    assertNotNull(_dataSource);
    assertNotNull(_jdbcTemplate);
    assertNotNull(_entityManager);
    assertNotNull(_userRepository);
  }

  @Test
  public void testBasicOperations() {
    User expectedUser = new User("alice", 1);
    _userRepository.saveAndFlush(expectedUser);
    Iterable<User> users = _userRepository.findAll();
    assertNotNull(users);
    for (User user : users) {
      assertEquals(expectedUser.getName(), user.getName());
      assertEquals(expectedUser.getSalary(), user.getSalary(), EPSILON);
    }
    _userRepository.deleteAllInBatch();
    users = _userRepository.findAll();
    for (User user : users) {
      fail();
      assertNull(user);
    }
  }

  @Test
  public void testSamePrimaryKeyIsUpdate() {
    String name = "alice";
    User expectedUser1 = new User(name, 1);
    User expectedUser2 = new User(name, 2);
    _userRepository.saveAndFlush(expectedUser1);
    _userRepository.saveAndFlush(expectedUser2);
    Optional<User> userOptional = _userRepository.findById(name);
    assertTrue(userOptional.isPresent());
    User user = userOptional.get();
    assertEquals(expectedUser2.getName(), user.getName());
    assertEquals(expectedUser2.getSalary(), user.getSalary(), EPSILON);
  }

  @Test(expected = ConstraintViolationException.class)
  public void testSalaryConstraint_MinFailed() {
    String name = "alice";
    User expectedUser = new User(name, -0.1);
    _userRepository.saveAndFlush(expectedUser);
  }

  @Test(expected = ConstraintViolationException.class)
  public void testSalaryConstraint_MaxFailed() {
    String name = "alice";
    User expectedUser = new User(name, 4000.1);
    _userRepository.saveAndFlush(expectedUser);
  }

  @Test
  public void testSalaryConstraint_MinSuccess() {
    String name = "alice";
    User expectedUser = new User(name, 0);
    _userRepository.saveAndFlush(expectedUser);
    Optional<User> userOptional = _userRepository.findById(name);
    assertTrue(userOptional.isPresent());
    User user = userOptional.get();
    assertEquals(expectedUser.getName(), user.getName());
    assertEquals(expectedUser.getSalary(), user.getSalary(), EPSILON);
  }

  @Test
  public void testSalaryConstraint_MaxSuccess() {
    String name = "alice";
    User expectedUser = new User(name, 0);
    _userRepository.saveAndFlush(expectedUser);
    Optional<User> userOptional = _userRepository.findById(name);
    assertTrue(userOptional.isPresent());
    User user = userOptional.get();
    assertEquals(expectedUser.getName(), user.getName());
    assertEquals(expectedUser.getSalary(), user.getSalary(), EPSILON);
  }
}
