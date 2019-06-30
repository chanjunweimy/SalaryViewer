package cdit.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import cdit.exception.CditException;

public interface CsvParserService {
  public <T> List<T> parseInputStream(InputStream inputStream, CditCsvMapper<T> cditCsvMapper)
      throws CditException, IOException;
}
