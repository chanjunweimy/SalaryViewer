package cdit.service;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.MappingIterator;
import cdit.exception.CditException;

public interface CditCsvMapper<T> {
  public String[] getHeaders(MappingIterator<String[]> it) throws CditException;

  public Map<String, Integer> getHeaderIndices(String[] headers) throws CditException;

  public void validateHeaderIndices(Map<String, Integer> headerIndices) throws CditException;

  public T createObjectByRow(String[] row, Map<String, Integer> headerIndices) throws CditException;

  public void validateObject(T object) throws CditException;

  public void validateObjects(List<T> objects) throws CditException;
}
