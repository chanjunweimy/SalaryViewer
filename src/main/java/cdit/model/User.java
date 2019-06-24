package cdit.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
public class User {
  @Id
  @NotNull
  private String name;
  
  @NotNull
  @Min(0)
  @Max(4000)
  private double salary;

  protected User() {}
  
  public User(String name, double salary) {
    this.setName(name);  
    this.setSalary(salary);
  }
  
  public String getName() {
    return name;
  }

  public double getSalary() {
    return salary;
  }

  private void setName(String name) {
    this.name = name;
  }
  
  private void setSalary(double salary) {
    this.salary = salary;
  }
}
