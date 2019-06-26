package cdit.service;

import java.io.File;
import java.util.List;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import cdit.exception.InvalidCsvException;

@Service()
public class CsvParserServiceImpl implements CsvParserService {

  @Override
  public <T> List<T> loadObjectList(Class<T> type, File file) throws InvalidCsvException {
    try {
      CsvSchema bootstrapSchema = CsvSchema.emptySchema().withHeader();
      CsvMapper mapper = new CsvMapper();
      MappingIterator<T> readValues = 
        mapper.readerFor(type).with(bootstrapSchema).readValues(file);
      return readValues.readAll();
    } catch (Exception e) {
      throw new InvalidCsvException();
    }
  }

}
