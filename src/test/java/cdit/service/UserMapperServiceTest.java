package cdit.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
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
import cdit.exception.CditException;
import cdit.exception.UserDuplicateException;
import cdit.exception.UserMissingCsvHeaderException;
import cdit.exception.UserNameIsEmptyException;
import cdit.exception.UserSalaryInvalidTypeException;
import cdit.exception.UserSalaryOutOfRangeException;
import cdit.model.User;
import cdit.util.TestHelper;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ComponentScan(basePackages = "cdit",
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SwaggerConfig.class))
@org.springframework.transaction.annotation.Transactional()
public class UserMapperServiceTest {
  private static final double EPSILON = 0.001;

  @Autowired
  private CsvParserService _csvParserService;

  @Autowired
  private UserMapperService _userMapperService;

  @Rule
  public TemporaryFolder _folder = new TemporaryFolder();

  @Test
  public void testInjectedComponentsAreNotNull() {
    assertNotNull(_csvParserService);
    assertNotNull(_userMapperService);
  }

  @Test
  public void testValidUsers() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    List<User> actualUsers = getUsersFromCsv(fileLines);

    assertEquals(expectedStringArrays.size() - 1, actualUsers.size());

    for (int i = 0; i < actualUsers.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i + 1);
      User actualUser = actualUsers.get(i);
      assertEquals(expectedStringArray[0].trim(), actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[1]), actualUser.getSalary(), EPSILON);
    }
  }

  @Test
  public void testValidUsersDifferentColumnOrder() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"salary", "name"});
    expectedStringArrays.add(new String[] {"2500.05", "John"});
    expectedStringArrays.add(new String[] {"4000.00", "Mary Posa"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    List<User> actualUsers = getUsersFromCsv(fileLines);

    assertEquals(expectedStringArrays.size() - 1, actualUsers.size());

    for (int i = 0; i < actualUsers.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i + 1);
      User actualUser = actualUsers.get(i);
      assertEquals(expectedStringArray[1].trim(), actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[0]), actualUser.getSalary(), EPSILON);
    }
  }

  @Test
  public void testValidUsersAdditionalColumn() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"salary", "name", "a"});
    expectedStringArrays.add(new String[] {"2500.05", "John", "a"});
    expectedStringArrays.add(new String[] {"4000.00", "Mary Posa", "a"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    List<User> actualUsers = getUsersFromCsv(fileLines);

    assertEquals(expectedStringArrays.size() - 1, actualUsers.size());

    for (int i = 0; i < actualUsers.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i + 1);
      User actualUser = actualUsers.get(i);
      assertEquals(expectedStringArray[1].trim(), actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[0]), actualUser.getSalary(), EPSILON);
    }
  }

  @Test
  public void testValidUsersWithSpacedHeaders() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name    ", "    salary"});
    expectedStringArrays.add(new String[] {"John", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    List<User> actualUsers = getUsersFromCsv(fileLines);

    assertEquals(expectedStringArrays.size() - 1, actualUsers.size());

    for (int i = 0; i < actualUsers.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i + 1);
      User actualUser = actualUsers.get(i);
      assertEquals(expectedStringArray[0].trim(), actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[1]), actualUser.getSalary(), EPSILON);
    }
  }

  @Test
  public void testValidUsersWithMixedCaseHeaders() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"NAme", "salaRY"});
    expectedStringArrays.add(new String[] {"John", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    List<User> actualUsers = getUsersFromCsv(fileLines);

    assertEquals(expectedStringArrays.size() - 1, actualUsers.size());

    for (int i = 0; i < actualUsers.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i + 1);
      User actualUser = actualUsers.get(i);
      assertEquals(expectedStringArray[0].trim(), actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[1]), actualUser.getSalary(), EPSILON);
    }
  }

  @Test(expected = UserMissingCsvHeaderException.class)
  public void testInvalidUsersWithMissingHeaderLine() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    getUsersFromCsv(fileLines);
  }

  @Test(expected = UserMissingCsvHeaderException.class)
  public void testInvalidUsersWithMissingUserHeader() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"SALary"});
    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    getUsersFromCsv(fileLines);
  }

  @Test(expected = UserMissingCsvHeaderException.class)
  public void testInvalidUsersWithMissingSalaryHeader() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name"});
    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    getUsersFromCsv(fileLines);
  }

  @Test(expected = UserNameIsEmptyException.class)
  public void testInvalidUsersWithInvalidName() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"", "2500.05"});
    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    getUsersFromCsv(fileLines);
  }

  @Test(expected = UserDuplicateException.class)
  public void testInvalidUsersWithDuplicateUser() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"bob", "2500.05"});
    expectedStringArrays.add(new String[] {"bob", "2500.10"});
    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    getUsersFromCsv(fileLines);
  }

  @Test(expected = UserDuplicateException.class)
  public void testInvalidUsersWithDuplicateUserNameWithDifferentCase() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"bob", "2500.05"});
    expectedStringArrays.add(new String[] {"Bob", "2500.10"});
    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    getUsersFromCsv(fileLines);
  }

  @Test(expected = UserSalaryInvalidTypeException.class)
  public void testInvalidUsersWithInvalidSalaryType() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"bob", "true"});
    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    getUsersFromCsv(fileLines);
  }

  @Test(expected = UserSalaryInvalidTypeException.class)
  public void testInvalidUsersWithEmptySalary() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"bob", ""});
    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    getUsersFromCsv(fileLines);
  }

  @Test(expected = UserSalaryOutOfRangeException.class)
  public void testInvalidUsersWithUserSalaryOutOfMinRange() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"bob", "-0.1"});
    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    getUsersFromCsv(fileLines);
  }

  @Test(expected = UserSalaryOutOfRangeException.class)
  public void testInvalidUsersWithUserSalaryOutOfMaxRange() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"bob", "4000.1"});
    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    getUsersFromCsv(fileLines);
  }
  
  @Test
  public void testGetHeaderIndices() throws CditException {
    String[] row = new String[] { "name", "a", "salary" };
    Map<String, Integer> headerIndices = _userMapperService.getHeaderIndices(row);
    for (int i = 0; i < row.length; i++) {
      assertEquals(i, headerIndices.get(row[i]).intValue());
    }
  }
  
  @Test
  public void testValidateHeaderIndicesSuccess() throws CditException {
    Map<String, Integer> headerIndices = new Hashtable<String, Integer>();
    headerIndices.put("name", 0);
    headerIndices.put("salary", 1);
    _userMapperService.validateHeaderIndices(headerIndices);
  }
  
  @Test(expected = UserMissingCsvHeaderException.class)
  public void testValidateHeaderIndicesFailedDueToMissingName() throws CditException {
    Map<String, Integer> headerIndices = new Hashtable<String, Integer>();
    headerIndices.put("salary", 1);
    _userMapperService.validateHeaderIndices(headerIndices);
  }
  
  @Test(expected = UserMissingCsvHeaderException.class)
  public void testValidateHeaderIndicesFailedDueToMissingSalary() throws CditException {
    Map<String, Integer> headerIndices = new Hashtable<String, Integer>();
    headerIndices.put("name", 0);
    _userMapperService.validateHeaderIndices(headerIndices);
  }

  @Test
  public void testCreateObjectByValidRow() throws CditException {
    Map<String, Integer> headerIndices = new Hashtable<String, Integer>();
    headerIndices.put("name", 0);
    headerIndices.put("salary", 1);
    String[] row = new String[] { "bob", "3000" };
    User user = _userMapperService.createObjectByRow(row, headerIndices);
    assertEquals(row[headerIndices.get("name").intValue()], user.getName());
    assertEquals(Double.parseDouble(row[headerIndices.get("salary").intValue()]), 
                 user.getSalary(), EPSILON);
  }
  
  @Test(expected = UserSalaryInvalidTypeException.class)
  public void testCreateObjectByRowWithInvalidSalary() throws CditException {
    Map<String, Integer> headerIndices = new Hashtable<String, Integer>();
    headerIndices.put("name", 0);
    headerIndices.put("salary", 1);
    String[] row = new String[] { "bob", "true" };
    _userMapperService.createObjectByRow(row, headerIndices);
  }
  
  @Test
  public void testValidateObjectSuccess() throws CditException {
    User user = new User("bob", 3000);
    _userMapperService.validateObject(user);
  }
  
  @Test(expected = UserNameIsEmptyException.class)
  public void testValidateObjectFailedDueToEmptyName() throws CditException {
    User user = new User("", 3000);
    _userMapperService.validateObject(user);
  }
  
  @Test(expected = UserSalaryOutOfRangeException.class)
  public void testValidateObjectFailedDueToSalaryMinRange() throws CditException {
    User user = new User("bob", -1);
    _userMapperService.validateObject(user);
  }
  
  @Test(expected = UserSalaryOutOfRangeException.class)
  public void testValidateObjectFailedDueToSalaryMaxRange() throws CditException {
    User user = new User("bob", 4000.1);
    _userMapperService.validateObject(user);
  }
  
  @Test
  public void testValidateObjectsSuccess() throws CditException {
    List<User> users = new ArrayList<User>();
    users.add(new User("alice", 2999));
    users.add(new User("bob", 2999));
    _userMapperService.validateObjects(users);
  }
  
  @Test(expected = UserDuplicateException.class)
  public void testValidateObjectsFailedDueToDuplicateNames() throws CditException {
    List<User> users = new ArrayList<User>();
    users.add(new User("bob", 1));
    users.add(new User("bob", 2999));
    _userMapperService.validateObjects(users);
  }
  
  private List<User> getUsersFromCsv(List<String> fileLines) throws Exception {
    return TestHelper.getObjectUsingFileInputStream(_folder, fileLines,
        (InputStream inputStream) -> _csvParserService.parseInputStream(inputStream,
            _userMapperService));

  }
}
