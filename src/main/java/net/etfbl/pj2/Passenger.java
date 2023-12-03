package net.etfbl.pj2;

import java.io.Serializable;

public class Passenger implements Serializable {
  private String firstName;
  private String lastName;
  private Document document;

  public Passenger(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.document = null;
  }

  public Passenger(String firstName, String lastName, String ID) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.document = new Document(firstName, lastName, ID, true);
  }

  public boolean hasValidDocument() {
    return document.isValid();
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  public String getID() {
    return document.getID();
  }

  public Document getDocument() {
    return document;
  }

  public void setDocument(Document document) {
    this.document = document;
  }

  @Override
  public String toString() {
    return getFullName() + " - ID: " + document.getID() + " - " + (document.isValid() ? "is" : "isn't") + " valid";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj != null && getClass() == obj.getClass()) {
      Passenger temp = (Passenger) obj;
      if (firstName == temp.firstName && lastName == temp.lastName && document.getID() == temp.document.getID())
        return true;
    }
    return false;
  }
}
