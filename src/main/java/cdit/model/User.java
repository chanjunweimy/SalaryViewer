package cdit.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class User {
  public static final double SALARY_MIN = 0.0;
  public static final double SALARY_MAX = 4000.0;

  @Id
  @NotNull
  private String _name;

  @NotNull
  @Min((long) SALARY_MIN)
  @Max((long) SALARY_MAX)
  private double _salary;

  protected User() {}

  public User(String name, double salary) {
    this.setName(name);
    this.setSalary(salary);
  }

  public String getName() {
    return _name;
  }

  public double getSalary() {
    return _salary;
  }

  private void setName(String name) {
    this._name = name;
  }

  private void setSalary(double salary) {
    this._salary = salary;
  }
}
