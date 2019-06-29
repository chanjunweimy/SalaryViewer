package cdit.service;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
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
  public void testValidCsv() throws InvalidCsvException {
    final String fileName = "valid.csv";
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});
    expectedStringArrays.add(new String[] {"Mike", "4001.00"});

    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileName, fileLines);
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
  public void testValidCsvWithEmptyFields() throws InvalidCsvException {
    final String fileName = "validEmptyFields.csv";
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {""});
    expectedStringArrays.add(new String[] {""});

    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileName, fileLines);
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
   * 
   * @throws InvalidCsvException
   */
  @Test
  public void testValidCsvWithSpacing() throws InvalidCsvException {
    final String fileName = "valid.csv";
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John    ", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});
    expectedStringArrays.add(new String[] {"Mike", "4001.00"});

    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileName, fileLines);
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
   * 
   * @throws InvalidCsvException
   */
  @Test
  public void testValidCsvWithQuotesAndNewLine() throws InvalidCsvException {
    final String fileName = "valid.csv";
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"\"John  \nDoe\"", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});
    expectedStringArrays.add(new String[] {"Mike", "4001.00"});

    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileName, fileLines);
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
  public void testValidCsvWithQuotesAndComa() throws InvalidCsvException {
    final String fileName = "valid.csv";
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"\"John,Doe\"", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});
    expectedStringArrays.add(new String[] {"Mike", "4001.00"});

    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileName, fileLines);
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
  public void testMultiColumnCsvWithEmptyValues() throws InvalidCsvException {
    final String fileName = "valid.csv";
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"", ""});

    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileName, fileLines);
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
  public void testEmptyCsv() throws InvalidCsvException {
    final String fileName = "empty.csv";
    List<String[]> expectedStringArrays = Arrays.asList();
    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    List<String[]> actualStringArrays = GetStringArraysFromCsv(fileName, fileLines);
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
  public void testInvalidCsvWithInconsistentNumberOfColumns() throws InvalidCsvException {
    final String fileName = "invalid.csv";
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"John", "2500.05"});
    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    fileLines.add("a,1,a");
    GetStringArraysFromCsv(fileName, fileLines);
  }

  @Test(expected = InvalidCsvException.class)
  public void testMultiColumnCsvWithEmptyLine() throws InvalidCsvException {
    final String fileName = "valid.csv";
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"John", "2500.05"});
    expectedStringArrays.add(new String[] {"Mary Posa", "4000.00"});
    expectedStringArrays.add(new String[] {"Mike", "4001.00"});

    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    fileLines.add(2, "");
    GetStringArraysFromCsv(fileName, fileLines);
  }

  /**
   * Double quotes are not allowed in unquoted fields according to RFC 4180
   * 
   * @throws InvalidCsvException
   */
  @Test(expected = InvalidCsvException.class)
  public void testInvalidCsvDoubleQuoteInUnquotedField() throws InvalidCsvException {
    final String fileName = "invalid.csv";
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"Jo\"hn", "2500.05"});
    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    GetStringArraysFromCsv(fileName, fileLines);
  }

  /**
   * Not supported by com.fasterxml.jackson
   * 
   * @throws InvalidCsvException
   */
  @Test(expected = InvalidCsvException.class)
  public void testCsvWithQuotesAndPairedEmbeddedQuotes() throws InvalidCsvException {
    final String fileName = "valid.csv";
    List<String[]> expectedStringArrays = new ArrayList<String[]>();
    expectedStringArrays.add(new String[] {"name", "salary"});
    expectedStringArrays.add(new String[] {"\"\"John Doe\"\"", "2500.05"});

    List<String> fileLines = GetCsvFileLinesFromStringArrays(expectedStringArrays);
    GetStringArraysFromCsv(fileName, fileLines);
  }

  private List<String> GetCsvFileLinesFromStringArrays(List<String[]> stringArrays) {
    List<String> fileLines = new ArrayList<String>();
    for (String[] stringArray : stringArrays) {
      StringBuffer buffer = new StringBuffer();
      buffer.append(stringArray[0]);
      for (int i = 1; i < stringArray.length; i++) {
        buffer.append(",");
        buffer.append(stringArray[i]);
      }
      fileLines.add(buffer.toString());
    }
    return fileLines;
  }

  private List<String[]> GetStringArraysFromCsv(String fileName, List<String> fileLines)
      throws InvalidCsvException {
    File file = null;
    try {
      file = _folder.newFile(fileName);
      FileWriter writer = new FileWriter(file);
      for (String line : fileLines) {
        writer.write(line + System.lineSeparator());
      }
      writer.close();

      InputStream inputStream = new FileInputStream(file);
      return _csvParserService.loadStringArrays(inputStream);

    } catch (IOException e) {
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
