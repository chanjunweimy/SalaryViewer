package cdit.service;

import java.io.File;
import java.util.List;
import cdit.exception.InvalidCsvException;

public interface CsvParserService {
  public <T> List<T> loadObjectList(Class<T> type, File file) throws InvalidCsvException;
}
