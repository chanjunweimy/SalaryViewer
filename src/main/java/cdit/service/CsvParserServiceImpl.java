package cdit.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import cdit.exception.CditException;
import cdit.exception.InvalidCsvException;

@Service()
public class CsvParserServiceImpl implements CsvParserService {
  private Logger _logger = LoggerFactory.getLogger(CsvParserServiceImpl.class);

  @Override
  public <T> List<T> parseInputStream(InputStream inputStream, CditCsvMapper<T> cditCsvMapper)
      throws CditException, IOException {
    CsvMapper mapper = getCsvMapper();
    List<T> data = new ArrayList<T>();

    MappingIterator<String[]> it = mapper.readerFor(String[].class).readValues(inputStream);
    Map<String, Integer> headerIndices = null;

    String[] headers = cditCsvMapper.getHeaders(it);
    headerIndices = cditCsvMapper.getHeaderIndices(headers);
    cditCsvMapper.validateHeaderIndices(headerIndices);

    while (true) {
      String[] row;
      try {
        if (!it.hasNext()) {
          break;
        }
        row = it.next();
      } catch (Exception e) {
        _logger.warn(e.getMessage());
        throw new InvalidCsvException();
      }
      validateCsvRowRequirement(headers, row);

      T object = cditCsvMapper.createObjectByRow(row, headerIndices);
      cditCsvMapper.validateObject(object);
      data.add(object);
    }
    cditCsvMapper.validateObjects(data);

    return data;
  }

  private CsvMapper getCsvMapper() {
    CsvFactory csvFactory = new CsvFactory();
    csvFactory.enable(CsvParser.Feature.WRAP_AS_ARRAY);
    CsvMapper mapper = new CsvMapper(csvFactory);
    return mapper;
  }

  private void validateCsvRowRequirement(String[] headers, String[] row)
      throws InvalidCsvException {
    if (!isDoubleQuotesInFieldMeetsCsvRequirementInRow(row) || isColumnLengthEqual(headers, row)) {
      throw new InvalidCsvException();
    }
  }

  private boolean isColumnLengthEqual(String[] headers, String[] row) {
    return row.length != headers.length;
  }

  private boolean isDoubleQuotesInFieldMeetsCsvRequirementInRow(String[] row) {
    for (String field : row) {
      if (!isDoubleQuotesInFieldMeetsCsvRequirement(field)) {
        return false;
      }
    }
    return true;
  }

  private boolean isDoubleQuotesInFieldMeetsCsvRequirement(String field) {
    if (isFieldQuoted(field)) {
      return true;
    }
    return !field.contains("\"");
  }

  private boolean isFieldQuoted(String field) {
    return field.startsWith("\"") && field.endsWith("\"");
  }
}
