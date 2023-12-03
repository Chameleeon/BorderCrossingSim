package net.etfbl.pj2;

import java.io.*;
import java.util.ArrayList;

public class Incident implements Serializable {
  private ArrayList<Passenger> problematicPassengers;
  private String vehicleID;
  private boolean vehiclePassed;
  private String description;
  private String vehicleType;
  private String icon;
  private String incidentType;

  public Incident(String incidentType, ArrayList<Passenger> problematicPassengers, String vehicleID,
      boolean vehiclePassed, String description, String vehicleType, String icon) {
    this.incidentType = incidentType;
    this.problematicPassengers = problematicPassengers;
    this.vehicleID = vehicleID;
    this.vehiclePassed = vehiclePassed;
    this.description = description;
    this.vehicleType = vehicleType;
    this.icon = icon;
  }

  public ArrayList<Passenger> getProblematicPassengers() {
    return problematicPassengers;
  }

  @Override
  public String toString() {
    String str = incidentType + ":" + icon + ":" + vehicleType + ":" + vehicleID + ":" + description + ":"
        + vehiclePassed;
    for (Passenger passenger : problematicPassengers) {
      str += ":" + passenger.getID() + "_" + passenger.getFirstName() + "_" + passenger.getLastName();
    }

    return str;
  }

  public String getID() {
    return vehicleID;
  }

  public boolean passed() {
    return vehiclePassed;
  }

  public String getDesc() {
    return description;
  }

  public String getVehicleType() {
    return vehicleType;
  }

  public String getIncidentType() {
    return incidentType;
  }

  public String getIcon() {
    return icon;
  }

}
