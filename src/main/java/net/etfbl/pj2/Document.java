package net.etfbl.pj2;

import java.io.Serializable;

public class Document implements Serializable {
  private String firstName;
  private String lastName;
  private String ID;
  private boolean isValid;

  public Document(String firstName, String lastName, String ID, boolean isValid) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.ID = ID;
    this.isValid = isValid;
  }

  String getFirstName() {
    return firstName;
  }

  String getLastName() {
    return lastName;
  }

  String getID() {
    return ID;
  }

  boolean isValid() {
    return isValid;
  }
}
