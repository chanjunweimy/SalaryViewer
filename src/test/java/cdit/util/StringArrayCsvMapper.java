package cdit.util;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.MappingIterator;
import cdit.exception.CditException;
import cdit.service.CditCsvMapper;

public class StringArrayCsvMapper implements CditCsvMapper<String[]> {
  private String[] _headers;
  
  public StringArrayCsvMapper(String[] headers) {
    _headers = headers;
  }  
  
  @Override
  public String[] getHeaders(MappingIterator<String[]> it) throws CditException {
    return _headers;
  }

  @Override
  public Map<String, Integer> getHeaderIndices(String[] headers) throws CditException {
    return null;
  }

  @Override
  public void validateHeaderIndices(Map<String, Integer> headerIndices) throws CditException {
    // Not doing any validation on test
  }

  @Override
  public String[] createObjectByRow(String[] row, Map<String, Integer> headerIndices)
      throws CditException {
    return row;
  }

  @Override
  public void validateObject(String[] object) throws CditException {
    // Not doing any validation on test
  }

  @Override
  public void validateObjects(List<String[]> objects) throws CditException {
    // Not doing any validation on test
  }

}
