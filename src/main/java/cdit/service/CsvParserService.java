package cdit.service;

import java.io.InputStream;
import java.util.List;
import cdit.exception.InvalidCsvException;

public interface CsvParserService {
  public List<String[]> loadStringArrays(InputStream inputStream) throws InvalidCsvException;
}
