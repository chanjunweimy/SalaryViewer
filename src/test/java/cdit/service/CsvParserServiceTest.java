package cdit.service;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
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
import cdit.exception.InvalidCsvException;
import cdit.model.User;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ComponentScan(basePackages = "cdit", excludeFilters=@Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SwaggerConfig.class))
@org.springframework.transaction.annotation.Transactional()
public class CsvParserServiceTest {
  private static final double EPSILON = 0.001;
  
  @Autowired private CsvParserService _csvParserService;
  
  @Rule
  public TemporaryFolder _folder = new TemporaryFolder();

  @Test
  public void testInjectedComponentsAreNotNull(){
    assertNotNull(_csvParserService);
  }
  
  @Test
  public void testValidCsv() throws InvalidCsvException{
    final String fileName = "valid.csv";   
    List<User> expectedUsers = Arrays.asList(new User("John", 2500.05), new User("Mary Posa", 4000.00), new User("Mike", 4001.00));
    List<String> fileLines = GetCsvFileLinesFromUsers(expectedUsers);
    List<User> actualUsers = GetUsersFromCsv(fileName, fileLines); 
    assertEquals(expectedUsers.size(), actualUsers.size());
    for (User actualUser : actualUsers) {
      for (User expectedUser : expectedUsers) {
        if (expectedUser.getName().equals(actualUser.getName())) {
          assertEquals(expectedUser.getSalary(), actualUser.getSalary(), EPSILON);
        }
      }
    }
  }  
  
  @Test
  public void testEmptyCsv() throws InvalidCsvException{
    final String fileName = "empty.csv";   
    List<User> expectedUsers = Arrays.asList();
    List<String> fileLines = GetCsvFileLinesFromUsers(expectedUsers);
    List<User> actualUsers = GetUsersFromCsv(fileName, fileLines); 
    assertEquals(expectedUsers.size(), actualUsers.size());
    for (User actualUser : actualUsers) {
      for (User expectedUser : expectedUsers) {
        if (expectedUser.getName().equals(actualUser.getName())) {
          assertEquals(expectedUser.getSalary(), actualUser.getSalary(), EPSILON);
        }
      }
    }
  } 
  
  @Test(expected=InvalidCsvException.class)
  public void testInvalidCsv() throws InvalidCsvException{
    final String fileName = "invalid.csv";   
    List<User> expectedUsers = Arrays.asList(new User("John", 2500.05));
    List<String> fileLines = GetCsvFileLinesFromUsers(expectedUsers);
    fileLines.add(",,");
    GetUsersFromCsv(fileName, fileLines); 
  } 
  
  @Test(expected=InvalidCsvException.class)
  public void testInvalidCsvWithInvalidType() throws InvalidCsvException{
    final String fileName = "invalid.csv";   
    List<User> expectedUsers = Arrays.asList(new User("John", 2500.05));
    List<String> fileLines = GetCsvFileLinesFromUsers(expectedUsers);
    fileLines.add("bob,true");
    GetUsersFromCsv(fileName, fileLines); 
  } 
  
  private List<String> GetCsvFileLinesFromUsers(List<User> users) {
    List<String> fileLines = new ArrayList<String>();
    fileLines.add("name,salary");
    for (User user : users) {
      fileLines.add("\"" + user.getName() + "\"," + user.getSalary());
    }
    return fileLines;
  }
  
  private List<User> GetUsersFromCsv(String fileName, List<String> fileLines) throws InvalidCsvException {
    File file = null;
    try {
      file = _folder.newFile(fileName);
      FileWriter writer = new FileWriter(file);
      for (String line : fileLines) {
        writer.write(line + System.lineSeparator());
      }
      writer.close();
      
      return _csvParserService.loadObjectList(User.class, file);
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      fail();
    } finally {
      if (file != null) {
        file.delete();
      }      
    }
    return null;   
  }
}
