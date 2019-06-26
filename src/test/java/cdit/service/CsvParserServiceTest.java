package cdit.service;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
  @Autowired private CsvParserService _csvParserService;
  
  @Rule
  public TemporaryFolder _folder = new TemporaryFolder();

  @Test
  public void testInjectedComponentsAreNotNull(){
    assertNotNull(_csvParserService);
  }
  
  @Test
  public void testValidCsv() throws InvalidCsvException{
    File file = null;
    final String fileName = "valid.csv";
    List<String> fileLines = Arrays.asList("name,salary", "John,2500.05", "Mary Posa, 4000.00", "Mike,4001.00");
    try {
      file = _folder.newFile(fileName);
      FileWriter writer = new FileWriter(file);
      for (String line : fileLines) {
        writer.write(line + System.lineSeparator());
      }
      writer.close();
      
      _csvParserService.loadObjectList(User.class, file);
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      fail();
    } finally {
      if (file != null) {
        file.delete();
      }      
    }    
  }
}
