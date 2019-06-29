package cdit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import cdit.exception.UserDuplicateException;
import cdit.exception.UserListValidationException;
import cdit.exception.UserMissingCsvHeaderException;
import cdit.exception.UserNameIsEmptyException;
import cdit.exception.UserSalaryInvalidTypeException;
import cdit.exception.UserSalaryOutOfRangeException;
import cdit.model.User;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ComponentScan(basePackages = "cdit",
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SwaggerConfig.class))
@org.springframework.transaction.annotation.Transactional()
public class UserMapperServiceTest {
  private static final double EPSILON = 0.001;

  @Autowired
  private UserMapperService _userMapperService;

  @Test
  public void testInjectedComponentsAreNotNull() {
    assertNotNull(_userMapperService);
  }

  @Test
  public void testValidUsers() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"name", "salary"});
    stringArrays.add(new String[] {"John", "2500.05"});
    stringArrays.add(new String[] {"Mary Posa", "4000.00"});
    stringArrays.add(new String[] {"Mike", "0.00"});

    List<User> actualUsers = _userMapperService.mapStringArraysToUsers(stringArrays);
    assertEquals(stringArrays.size() - 1, actualUsers.size());

    for (int i = 0; i < actualUsers.size(); i++) {
      String[] expectedStringArray = stringArrays.get(i + 1);
      User actualUser = actualUsers.get(i);
      assertEquals(expectedStringArray[0].trim(), actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[1]), actualUser.getSalary(), EPSILON);
    }
  }

  @Test
  public void testValidUsersDifferentColumnOrder() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"salary", "name"});
    stringArrays.add(new String[] {"2500.05", "John"});
    stringArrays.add(new String[] {"4000.00", "Mary Posa"});
    stringArrays.add(new String[] {"2999.00", "Mike"});
    int salaryColumnIndex = 0;
    int nameColumnIndex = 1;

    List<User> actualUsers = _userMapperService.mapStringArraysToUsers(stringArrays);
    assertEquals(stringArrays.size() - 1, actualUsers.size());

    for (int i = 0; i < actualUsers.size(); i++) {
      String[] expectedStringArray = stringArrays.get(i + 1);
      User actualUser = actualUsers.get(i);
      assertEquals(expectedStringArray[nameColumnIndex].trim(), actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[salaryColumnIndex]),
          actualUser.getSalary(), EPSILON);
    }
  }

  @Test
  public void testValidUsersAdditionalColumn() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"name", "salary", "a"});
    stringArrays.add(new String[] {"John", "2500.05", ""});
    stringArrays.add(new String[] {"Mary Posa", "4000.00", ""});
    stringArrays.add(new String[] {"Mike", "2999.00", ""});

    List<User> actualUsers = _userMapperService.mapStringArraysToUsers(stringArrays);
    assertEquals(stringArrays.size() - 1, actualUsers.size());

    for (int i = 0; i < actualUsers.size(); i++) {
      String[] expectedStringArray = stringArrays.get(i + 1);
      User actualUser = actualUsers.get(i);
      assertEquals(expectedStringArray[0].trim(), actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[1]), actualUser.getSalary(), EPSILON);
    }
  }

  @Test
  public void testValidUsersWithSpacedHeaders() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"name    ", "    salary"});
    stringArrays.add(new String[] {"John", "2500.05"});
    stringArrays.add(new String[] {"Mary Posa", "4000.00"});
    stringArrays.add(new String[] {"Mike", "2999.00"});

    List<User> actualUsers = _userMapperService.mapStringArraysToUsers(stringArrays);
    assertEquals(stringArrays.size() - 1, actualUsers.size());

    for (int i = 0; i < actualUsers.size(); i++) {
      String[] expectedStringArray = stringArrays.get(i + 1);
      User actualUser = actualUsers.get(i);
      assertEquals(expectedStringArray[0].trim(), actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[1]), actualUser.getSalary(), EPSILON);
    }
  }

  @Test
  public void testValidUsersWithMixedCaseHeaders() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"Name", "SALary"});
    stringArrays.add(new String[] {"John", "2500.05"});
    stringArrays.add(new String[] {"Mary Posa", "4000.00"});
    stringArrays.add(new String[] {"Mike", "2999.00"});

    List<User> actualUsers = _userMapperService.mapStringArraysToUsers(stringArrays);
    assertEquals(stringArrays.size() - 1, actualUsers.size());

    for (int i = 0; i < actualUsers.size(); i++) {
      String[] expectedStringArray = stringArrays.get(i + 1);
      User actualUser = actualUsers.get(i);
      assertEquals(expectedStringArray[0].trim(), actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[1]), actualUser.getSalary(), EPSILON);
    }
  }

  @Test(expected = UserMissingCsvHeaderException.class)
  public void testInvalidUsersWithMissingHeaderLine() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    _userMapperService.mapStringArraysToUsers(stringArrays);
  }

  @Test(expected = UserMissingCsvHeaderException.class)
  public void testInvalidUsersWithMissingUserHeader() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"SALary"});
    _userMapperService.mapStringArraysToUsers(stringArrays);
  }

  @Test(expected = UserMissingCsvHeaderException.class)
  public void testInvalidUsersWithMissingSalaryHeader() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"user"});
    _userMapperService.mapStringArraysToUsers(stringArrays);
  }

  @Test(expected = UserNameIsEmptyException.class)
  public void testInvalidUsersWithInvalidName() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"name", "salary"});
    stringArrays.add(new String[] {"", "2500.05"});
    _userMapperService.mapStringArraysToUsers(stringArrays);
  }

  @Test(expected = UserDuplicateException.class)
  public void testInvalidUsersWithDuplicateUser() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"name", "salary"});
    stringArrays.add(new String[] {"bob", "2500.05"});
    stringArrays.add(new String[] {"bob", "2500.10"});
    _userMapperService.mapStringArraysToUsers(stringArrays);
  }

  @Test(expected = UserDuplicateException.class)
  public void testInvalidUsersWithDuplicateUserNameWithDifferentCase()
      throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"name", "salary"});
    stringArrays.add(new String[] {"bob", "2500.05"});
    stringArrays.add(new String[] {"Bob", "2500.10"});
    _userMapperService.mapStringArraysToUsers(stringArrays);
  }

  @Test(expected = UserSalaryInvalidTypeException.class)
  public void testInvalidUsersWithInvalidSalaryType() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"name", "salary"});
    stringArrays.add(new String[] {"bob", "true"});
    _userMapperService.mapStringArraysToUsers(stringArrays);
  }

  @Test(expected = UserSalaryInvalidTypeException.class)
  public void testInvalidUsersWithEmptySalary() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"name", "salary"});
    stringArrays.add(new String[] {"bob", ""});
    _userMapperService.mapStringArraysToUsers(stringArrays);
  }

  @Test(expected = UserSalaryOutOfRangeException.class)
  public void testInvalidUsersWithUserSalaryOutOfMinRange() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"name", "salary"});
    stringArrays.add(new String[] {"bob", "-0.1"});
    _userMapperService.mapStringArraysToUsers(stringArrays);
  }

  @Test(expected = UserSalaryOutOfRangeException.class)
  public void testInvalidUsersWithUserSalaryOutOfMaxRange() throws UserListValidationException {
    List<String[]> stringArrays = new ArrayList<String[]>();
    stringArrays.add(new String[] {"name", "salary"});
    stringArrays.add(new String[] {"bob", "4000.1"});
    _userMapperService.mapStringArraysToUsers(stringArrays);
  }
}
