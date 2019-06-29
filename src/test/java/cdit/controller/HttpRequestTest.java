package cdit.controller;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import cdit.RestExceptionHandler;
import cdit.SwaggerConfig;
import cdit.model.User;
import cdit.util.TestHelper;

@RunWith(SpringRunner.class)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ComponentScan(basePackages = "cdit",
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SwaggerConfig.class))
@org.springframework.transaction.annotation.Transactional()
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HttpRequestTest {
  private static final double EPSILON = 0.001;

  @LocalServerPort
  private int _port;

  @Autowired
  private TestRestTemplate _restTemplate;

  @Rule
  public TemporaryFolder _folder = new TemporaryFolder();

  @Test
  public void testGetUsersWhenNoData() {
    List<User> users = getUsersFromUserController();
    assertEquals(0, users.size());
  }

  @Test
  public void testUpdateUsersThenGetUsers() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John Doe", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    ResponseEntity<String> response = uploadCsvToUserController(fileLines);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    List<User> users = getUsersFromUserController();
    assertEquals(2, users.size());
    final int nameIndex = 0;
    final int salaryIndex = 1;

    for (int i = 0; i < users.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i + 1);
      User actualUser = users.get(i);
      assertEquals(expectedStringArray[nameIndex], actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[salaryIndex]), actualUser.getSalary(),
          EPSILON);
    }
  }
  
  @Test
  public void testInvalidCsv() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John Doe", "2500.05", "a"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    ResponseEntity<String> response = uploadCsvToUserController(fileLines);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(RestExceptionHandler.MSG_INVALID_CSV, response.getBody());
  }

  @Test
  public void testInvalidUpdateUsersWithoutHeader() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"John Doe", "2500.05"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    ResponseEntity<String> response = uploadCsvToUserController(fileLines);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(RestExceptionHandler.MSG_USER_MISSING_HEADER, response.getBody());
  }
  
  @Test
  public void testInvalidUpdateUsersWithEmptyName() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"", "2500.05"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    ResponseEntity<String> response = uploadCsvToUserController(fileLines);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(RestExceptionHandler.MSG_USER_NAME_EMPTY, response.getBody());
  }
  
  @Test
  public void testInvalidUpdateUsersWithDuplicateName() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John Doe", "2500.05"});
    expectedStringArrays.add(new String[] {"John Doe", "2500.15"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    ResponseEntity<String> response = uploadCsvToUserController(fileLines);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(RestExceptionHandler.MSG_USER_NAME_DUPLICATE, response.getBody());
  }
  
  @Test
  public void testInvalidUpdateUsersWithInvalidSalary() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John Doe", "true"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    ResponseEntity<String> response = uploadCsvToUserController(fileLines);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(RestExceptionHandler.MSG_USER_SALARY_INVALID, response.getBody());
  }
  
  @Test
  public void testInvalidUpdateUsersWithSalaryNotInRange() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John Doe", "-1"});

    List<String> fileLines = TestHelper.getCsvFileLinesFromStringArrays(expectedStringArrays);
    ResponseEntity<String> response = uploadCsvToUserController(fileLines);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertEquals(RestExceptionHandler.MSG_USER_SALARY_INVALID, response.getBody());
  }

  private ResponseEntity<String> uploadCsvToUserController(List<String> fileLines) throws Exception {
    return TestHelper.getTUsingFile(_folder, fileLines, (File file) -> {
      MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
      parameters.add("file", new FileSystemResource(file));

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.MULTIPART_FORM_DATA);

      HttpEntity<MultiValueMap<String, Object>> entity =
          new HttpEntity<MultiValueMap<String, Object>>(parameters, headers);

      ResponseEntity<String> response =
          _restTemplate.postForEntity(getUserUrl(), entity, String.class);

      return response;
    });

  }

  private List<User> getUsersFromUserController() {
    ResponseEntity<List<User>> response = _restTemplate.exchange(getUserUrl(), HttpMethod.GET, null,
        new ParameterizedTypeReference<List<User>>() {});
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<User> users = response.getBody();
    return users;
  }

  private String getUserUrl() {
    return String.format("http://localhost:%d/users", _port);
  }
}
