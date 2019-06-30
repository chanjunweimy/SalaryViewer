package cdit.service;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.MappingIterator;
import cdit.exception.UserMissingCsvHeaderException;
import cdit.exception.UserNameIsEmptyException;
import cdit.exception.UserSalaryInvalidTypeException;
import cdit.exception.UserDuplicateException;
import cdit.exception.UserSalaryOutOfRangeException;
import cdit.model.User;

@Service()
public class UserMapperServiceImpl implements UserMapperService {
  private static final String COLUMN_LOWERCASE_NAME = "name";
  private static final String COLUMN_LOWERCASE_SALARY = "salary";

  @Override
  public String[] getHeaders(MappingIterator<String[]> it) throws UserMissingCsvHeaderException {
    if (!hasHeader(it)) {
      throw new UserMissingCsvHeaderException();
    }
    return it.next();
  }

  @Override
  public Map<String, Integer> getHeaderIndices(String[] headers)
      throws UserMissingCsvHeaderException {
    Hashtable<String, Integer> headerIndices = new Hashtable<String, Integer>();
    for (int i = 0; i < headers.length; i++) {
      headerIndices.put(headers[i].trim().toLowerCase(), i);
    }
    return headerIndices;
  }

  @Override
  public void validateHeaderIndices(Map<String, Integer> headerIndices)
      throws UserMissingCsvHeaderException {
    if (!headerIndices.containsKey(COLUMN_LOWERCASE_NAME)
        || !headerIndices.containsKey(COLUMN_LOWERCASE_SALARY)) {
      throw new UserMissingCsvHeaderException();
    }
  }

  @Override
  public User createObjectByRow(String[] row, Map<String, Integer> headerIndices)
      throws UserSalaryInvalidTypeException {
    String name = row[headerIndices.get(COLUMN_LOWERCASE_NAME).intValue()].trim();
    String salaryStr = row[headerIndices.get(COLUMN_LOWERCASE_SALARY).intValue()].trim();
    double salary = convertStringToDouble(salaryStr);
    return new User(name, salary);
  }

  @Override
  public void validateObject(User object)
      throws UserNameIsEmptyException, UserSalaryOutOfRangeException {
    validateName(object.getName());
    validateSalary(object.getSalary());
  }

  public void validateObjects(List<User> objects) throws UserDuplicateException {
    HashSet<String> userNameSet = new HashSet<String>();
    for (User user : objects) {
      String nameInLowerCase = user.getName().toLowerCase();
      if (hasNameAppearedInSet(nameInLowerCase, userNameSet)) {
        throw new UserDuplicateException();
      }
      userNameSet.add(nameInLowerCase);
    }
  }

  private boolean hasHeader(MappingIterator<String[]> it) {
    return it.hasNext();
  }

  private double convertStringToDouble(String str) throws UserSalaryInvalidTypeException {
    assert (str != null);
    double d;
    try {
      d = Double.parseDouble(str);
    } catch (NumberFormatException ex) {
      throw new UserSalaryInvalidTypeException();
    }
    return d;
  }

  private void validateName(String name) throws UserNameIsEmptyException {
    if (name.isEmpty()) {
      throw new UserNameIsEmptyException();
    }
  }

  private void validateSalary(double salary) throws UserSalaryOutOfRangeException {
    if (!isSalaryWithinRange(salary)) {
      throw new UserSalaryOutOfRangeException();
    }
  }

  private boolean isSalaryWithinRange(double salary) {
    return User.SALARY_MIN <= salary && salary <= User.SALARY_MAX;
  }

  private boolean hasNameAppearedInSet(String nameInLowerCase, HashSet<String> userNameSet) {
    return userNameSet.contains(nameInLowerCase);
  }
}
