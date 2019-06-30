package cdit.util;

import static org.junit.Assert.fail;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.rules.TemporaryFolder;

public class TestHelper {
  public static List<String> getCsvFileLinesFromStringArrays(List<String[]> stringArrays) {
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

  public static <T> T getObjectUsingFile(TemporaryFolder folder, List<String> fileLines,
      FunctionWithException<File, T> functionUsingFile) throws Exception {
    File file = null;
    try {
      file = folder.newFile();
      FileWriter writer = new FileWriter(file);
      for (String line : fileLines) {
        writer.write(line + System.lineSeparator());
      }
      writer.close();

      return functionUsingFile.apply(file);

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

  public static <T> T getObjectUsingFileInputStream(TemporaryFolder folder, List<String> fileLines,
      FunctionWithException<InputStream, T> functionUsingInputStream) throws Exception {
    return getObjectUsingFile(folder, fileLines, (File file) -> {
      InputStream inputStream = new FileInputStream(file);
      return functionUsingInputStream.apply(inputStream);
    });
  }
}
