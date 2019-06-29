package cdit.service;

import static org.junit.Assert.*;
import java.io.InputStream;
import java.util.ArrayList;
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
import cdit.util.TestHelper;

@RunWith(SpringRunner.class)
@DataJpaTest
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ComponentScan(basePackages = "cdit",
    excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SwaggerConfig.class))
@org.springframework.transaction.annotation.Transactional()
public class CsvParserServiceTest {
  @Autowired
  private CsvParserService _csvParserService;

  @Rule
  public TemporaryFolder _folder = new TemporaryFolder();

  @Test
  public void testInjectedComponentsAreNotNull() {
    assertNotNull(_csvParserService);
  }

  @Test
  public void testValidCsv() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});

    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileLines);
    assertEquals(expectedStringArrays.size(), actualStringArrays.size());
    for (int i = 0; i < expectedStringArrays.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i);
      String[] actualStringArray = actualStringArrays.get(i);
      assertEquals(expectedStringArray.length, actualStringArray.length);
      for (int j = 0; j < expectedStringArray.length; j++) {
        assertEquals(expectedStringArray[j], actualStringArray[j]);
      }
    }
  }

  @Test
  public void testValidCsvWithEmptyFields() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {""});
    expectedStringArrays.add(new String[] {""});

    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileLines);
    assertEquals(expectedStringArrays.size(), actualStringArrays.size());
    for (int i = 0; i < expectedStringArrays.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i);
      String[] actualStringArray = actualStringArrays.get(i);
      assertEquals(expectedStringArray.length, actualStringArray.length);
      for (int j = 0; j < expectedStringArray.length; j++) {
        assertEquals(expectedStringArray[j], actualStringArray[j]);
      }
    }
  }

  /**
   * Trimming is forbidden by RFC 4180
   * @throws Exception 
   */
  @Test
  public void testValidCsvWithSpacing() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John    ", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});

    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileLines);
    assertEquals(expectedStringArrays.size(), actualStringArrays.size());
    for (int i = 0; i < expectedStringArrays.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i);
      String[] actualStringArray = actualStringArrays.get(i);
      assertEquals(expectedStringArray.length, actualStringArray.length);
      for (int j = 0; j < expectedStringArray.length; j++) {
        assertEquals(expectedStringArray[j], actualStringArray[j]);
      }
    }
  }

  /**
   * Fields with embedded line breaks must be quoted
   * @throws Exception 
   */
  @Test
  public void testValidCsvWithQuotesAndNewLine() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"\"John  \nDoe\"", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});

    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileLines);
    assertEquals(expectedStringArrays.size(), actualStringArrays.size());

    expectedStringArrays.get(1)[0] = expectedStringArrays.get(1)[0].replaceAll("\"", "");
    for (int i = 0; i < expectedStringArrays.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i);
      String[] actualStringArray = actualStringArrays.get(i);
      assertEquals(expectedStringArray.length, actualStringArray.length);
      for (int j = 0; j < expectedStringArray.length; j++) {
        assertEquals(expectedStringArray[j], actualStringArray[j]);
      }
    }
  }

  @Test
  public void testValidCsvWithQuotesAndComa() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"\"John,Doe\"", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});

    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileLines);
    assertEquals(expectedStringArrays.size(), actualStringArrays.size());

    expectedStringArrays.get(1)[0] = expectedStringArrays.get(1)[0].replaceAll("\"", "");
    for (int i = 0; i < expectedStringArrays.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i);
      String[] actualStringArray = actualStringArrays.get(i);
      assertEquals(expectedStringArray.length, actualStringArray.length);
      for (int j = 0; j < expectedStringArray.length; j++) {
        assertEquals(expectedStringArray[j], actualStringArray[j]);
      }
    }
  }

  @Test()
  public void testMultiColumnCsvWithEmptyValues() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"", ""});

    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileLines);
    assertEquals(expectedStringArrays.size(), actualStringArrays.size());
    for (int i = 0; i < expectedStringArrays.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i);
      String[] actualStringArray = actualStringArrays.get(i);
      assertEquals(expectedStringArray.length, actualStringArray.length);
      for (int j = 0; j < expectedStringArray.length; j++) {
        assertEquals(expectedStringArray[j], actualStringArray[j]);
      }
    }
  }

  @Test
  public void testEmptyCsv() throws Exception {
    List<String[]> expectedStringArrays = Arrays.asList();
    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileLines);
    assertEquals(expectedStringArrays.size(), actualStringArrays.size());
    for (int i = 0; i < expectedStringArrays.size(); i++) {
      String[] expectedStringArray = expectedStringArrays.get(i);
      String[] actualStringArray = actualStringArrays.get(i);
      assertEquals(expectedStringArray.length, actualStringArray.length);
      for (int j = 0; j < expectedStringArray.length; j++) {
        assertEquals(expectedStringArray[j], actualStringArray[j]);
      }
    }
  }

  @Test(expected = InvalidCsvException.class)
  public void testInvalidCsvWithInconsistentNumberOfColumns() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"John", "2500.05"});
    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    fileLines.add("a,1,a");
    GetStringArraysFromCsv(fileLines);
  }

  @Test(expected = InvalidCsvException.class)
  public void testMultiColumnCsvWithEmptyLine() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});

    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    fileLines.add(2, "");
    GetStringArraysFromCsv(fileLines);
  }

  /**
   * Double quotes are not allowed in unquoted fields according to RFC 4180
   * @throws Exception 
   */
  @Test(expected = InvalidCsvException.class)
  public void testInvalidCsvDoubleQuoteInUnquotedField() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"Jo\"hn", "2500.05"});
    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    GetStringArraysFromCsv(fileLines);
  }

  /**
   * Not supported by com.fasterxml.jackson
   * @throws Exception 
   */
  @Test(expected = InvalidCsvException.class)
  public void testCsvWithQuotesAndPairedEmbeddedQuotes() throws Exception {
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"\"\"John Doe\"\"", "2500.05"});

    List<String> fileLines = TestHelper.GetCsvFileLinesFromStringArrays(expectedStringArrays);
    GetStringArraysFromCsv(fileLines);
  }

  private List<String[]> GetStringArraysFromCsv(List<String> fileLines)
      throws Exception {
    return TestHelper.GetTUsingFileInputStream(_folder, fileLines, 
        (InputStream inputStream) -> _csvParserService.loadStringArrays(inputStream));

  }
}
