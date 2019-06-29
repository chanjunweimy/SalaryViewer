package cdit.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import org.springframework.stereotype.Service;
import cdit.exception.UserMissingCsvHeaderException;
import cdit.exception.UserNameIsEmptyException;
import cdit.exception.UserSalaryInvalidTypeException;
import cdit.exception.UserDuplicateException;
import cdit.exception.UserListValidationException;
import cdit.exception.UserSalaryOutOfRangeException;
import cdit.model.User;

@Service()
public class UserMapperServiceImpl implements UserMapperService {
  private static final String COLUMN_LOWERCASE_NAME = "name";
  private static final String COLUMN_LOWERCASE_SALARY = "salary";

  @Override
  public List<User> mapStringArraysToUsers(List<String[]> stringArrays)
      throws UserListValidationException {
    validateHasHeader(stringArrays);

    String[] headers = stringArrays.get(0);
    Hashtable<String, Integer> headerIndices = getHeaderIndices(headers);
    validateHeader(headerIndices);

    HashSet<String> userNameSet = new HashSet<String>();
    List<User> users = new ArrayList<User>();
    for (int i = 1; i < stringArrays.size(); i++) {
      String[] row = stringArrays.get(i);

      String name = row[headerIndices.get(COLUMN_LOWERCASE_NAME).intValue()].trim();
      validateNameAndUpdateSet(name, userNameSet);

      String salaryStr = row[headerIndices.get(COLUMN_LOWERCASE_SALARY).intValue()].trim();
      double salary = convertStringToDouble(salaryStr);
      validateSalary(salary);

      users.add(new User(name, salary));
    }

    return users;
  }

  private void validateHasHeader(List<String[]> stringArrays) throws UserMissingCsvHeaderException {
    assert (stringArrays != null);
    if (!hasHeader(stringArrays)) {
      throw new UserMissingCsvHeaderException();
    }
  }

  private boolean hasHeader(List<String[]> stringArrays) {
    return stringArrays.size() > 0;
  }

  private Hashtable<String, Integer> getHeaderIndices(String[] headers)
      throws UserMissingCsvHeaderException {
    Hashtable<String, Integer> headerIndices = new Hashtable<String, Integer>();
    for (int i = 0; i < headers.length; i++) {
      headerIndices.put(headers[i].trim().toLowerCase(), i);
    }
    return headerIndices;
  }

  private void validateHeader(Hashtable<String, Integer> headerIndices)
      throws UserMissingCsvHeaderException {
    if (!headerIndices.containsKey(COLUMN_LOWERCASE_NAME)
        || !headerIndices.containsKey(COLUMN_LOWERCASE_SALARY)) {
      throw new UserMissingCsvHeaderException();
    }
  }

  private void validateNameAndUpdateSet(String name, HashSet<String> userNameSet)
      throws UserNameIsEmptyException, UserDuplicateException {
    assert (name != null && userNameSet != null);
    if (name.isEmpty()) {
      throw new UserNameIsEmptyException();
    }
    String nameInLowerCase = name.toLowerCase();
    if (hasNameAppearedInSet(nameInLowerCase, userNameSet)) {
      throw new UserDuplicateException();
    }
    userNameSet.add(nameInLowerCase);
  }

  private boolean hasNameAppearedInSet(String nameInLowerCase, HashSet<String> userNameSet) {
    return userNameSet.contains(nameInLowerCase);
  }

  private void validateSalary(double salary) throws UserSalaryOutOfRangeException {
    if (!isSalaryWithinRange(salary)) {
      throw new UserSalaryOutOfRangeException();
    }
  }

  private boolean isSalaryWithinRange(double salary) {
    return User.SALARY_MIN <= salary && salary <= User.SALARY_MAX;
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
}
