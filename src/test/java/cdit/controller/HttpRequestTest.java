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
  public void TestGetUsersWhenNoData() {
    List<User> users = GetUsersFromUserController();
    assertEquals(0, users.size());
  }
  
  @Test
  public void TestUpdateUsersThenGetUsers() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John Doe", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});

    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    HttpStatus status = UploadCsvToUserController(fileLines);
    assertEquals(HttpStatus.OK, status);
    
    List<User> users = GetUsersFromUserController();
    assertEquals(2, users.size());
    final int nameIndex = 0;
    final int salaryIndex = 1;
    
    for (int i = 0; i < users.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i + 1);
      User actualUser = users.get(i);
      assertEquals(expectedStringArray[nameIndex], actualUser.getName());
      assertEquals(Double.parseDouble(expectedStringArray[salaryIndex]), actualUser.getSalary(), EPSILON);
    }
  }
  
  @Test
  public void TestInvalidUpdateUsersWithoutHeader() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"John Doe", "2500.05"});

    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    HttpStatus status = UploadCsvToUserController(fileLines);
    assertEquals(HttpStatus.BAD_REQUEST, status);
  }
  
  private HttpStatus UploadCsvToUserController(List<String> fileLines)
      throws Exception {
    return TestHelper.GetTUsingFile(_folder, fileLines, 
        (File file) -> {
          MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();          
          parameters.add("file", new FileSystemResource(file));

          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.MULTIPART_FORM_DATA);

          HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<MultiValueMap<String, Object>>(parameters, headers);

          ResponseEntity<String> response = _restTemplate.postForEntity(GetUserUrl(), entity, String.class);

          return response.getStatusCode();
        });

  } 
  
  private List<User> GetUsersFromUserController() {
    ResponseEntity<List<User>> response = _restTemplate.exchange(
        GetUserUrl(),
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<List<User>>(){});
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<User> users = response.getBody();
    return users;
  }
  
  private String GetUserUrl() {
    return String.format("http://localhost:%d/users", _port);
  }
}
