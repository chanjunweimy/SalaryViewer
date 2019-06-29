package cdit.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import cdit.exception.InvalidCsvException;

@Service()
public class CsvParserServiceImpl implements CsvParserService {

  @Override
  public List<String[]> loadStringArrays(InputStream inputStream) throws InvalidCsvException {
    CsvFactory csvFactory = new CsvFactory();
    csvFactory.enable(CsvParser.Feature.WRAP_AS_ARRAY);
    CsvMapper mapper = new CsvMapper(csvFactory);
    MappingIterator<String[]> it;
    try {
      it = mapper.readerFor(String[].class).readValues(inputStream);
      List<String[]> data = new ArrayList<String[]>();
      int prevColumnNumber = -1;
      while (it.hasNext()) {
        String[] row = it.next();
        if (!isDoubleQuotesInFieldMeetsCsvRequirementInRow(row)) {
          throw new InvalidCsvException();
        }

        if (prevColumnNumber > 0 && row.length != prevColumnNumber) {
          throw new InvalidCsvException();
        }

        data.add(row);
        prevColumnNumber = row.length;
      }
      return data;
    } catch (Exception e) {
      throw new InvalidCsvException();
    }
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
