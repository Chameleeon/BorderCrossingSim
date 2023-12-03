package net.etfbl.pj2;

import java.util.logging.Level;
import java.util.ArrayList;
import java.util.Iterator;

public class PoliceTerminal implements BorderTerminal {

  private boolean isAvailable = true;
  private boolean isWorking = true;
  private String name;
  private boolean acceptsCars;
  private boolean acceptsBuses;
  private Boolean acceptsTrucks;

  public PoliceTerminal(String name, boolean acceptsCars, boolean acceptsBuses, boolean acceptsTrucks) {
    this.name = name;
    this.acceptsCars = acceptsCars;
    this.acceptsBuses = acceptsBuses;
    this.acceptsTrucks = acceptsTrucks;
    if (TerminalWatcher.terminalStatus.containsKey(name)) {
      isAvailable = TerminalWatcher.terminalStatus.get(name);
    } else {
      isAvailable = false;
    }
  }

  // returns true if the vehicle is clear to pass (i.e. the driver has a valid
  // passport)
  // otherwise, returns false
  private boolean checkPassports(PassengerVehicle vehicle, String vehicleType) {
    ArrayList<Passenger> passengers = vehicle.getPassengers();
    ArrayList<Passenger> problematicPassengers = new ArrayList<>();
    boolean passed;
    for (Iterator<Passenger> iterator = passengers.iterator(); iterator.hasNext();) {
      Passenger passenger = iterator.next();
      Document document = passenger.getDocument();
      if (!document.isValid()) {
        iterator.remove();
        problematicPassengers.add(passenger);
      }
    }
    if (problematicPassengers.size() > 0) {
      if (problematicPassengers.contains(vehicle.getDriver())) {
        System.out.println(name + ": " + vehicleType + " " + vehicle.getID()
            + " did not pass police terminal. Problematic passengers were: ");
        passed = false;
      } else {
        System.out.println(name + ": " + vehicleType + " " + vehicle.getID()
            + " passed police terminal. Problematic passengers were: ");
        passed = true;
      }
      for (Passenger passenger : problematicPassengers) {
        System.out.println(passenger.getFullName());
      }
      Incident incident = new Incident("police", problematicPassengers, vehicle.getID(), passed,
          "Passengers had invalid documents",
          vehicleType, vehicle.getIconName());
      FileUtils.serializeIncident(incident, "document_incidents_");
    } else {
      System.out
          .println(name + ": " + vehicleType + " " + vehicle.getID() + " passed police terminal without incident.");
      passed = true;
    }
    return passed;
  }

  @Override
  public boolean processVehicle(Car car) {
    int timePerPassenger = 500;
    try {
      System.out.println("Processing car on terminal " + name + " ...");
      car.checkPause();
      Thread.sleep(timePerPassenger * (car.getNumPassengers()));
      car.checkPause();
    } catch (InterruptedException e) {
      Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing car", e);
    }
    return checkPassports(car, car.getType());
  }

  @Override
  public boolean processVehicle(Bus bus) {
    int timePerPassenger = 100;
    try {
      System.out.println("Processing bus on terminal " + name + " ...");
      bus.checkPause();
      Thread.sleep(timePerPassenger * (bus.getNumPassengers()));
      bus.checkPause();
    } catch (InterruptedException e) {
      Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing bus", e);
    }
    return checkPassports(bus, bus.getType());
  }

  @Override
  public boolean processVehicle(Truck truck) {
    int timePerPassenger = 500;
    try {
      System.out.println("Processing truck on terminal " + name + " ...");
      truck.checkPause();
      Thread.sleep(timePerPassenger * (truck.getNumPassengers()));
      truck.checkPause();
    } catch (InterruptedException e) {
      Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing bus", e);
    }
    return checkPassports(truck, truck.getType());
  }

  @Override
  public boolean isAvailable() {
    return isAvailable;
  }

  public boolean isWorking() {
    return isWorking;
  }

  public void changeTerminalStatus(boolean status) {
    this.isWorking = status;
  }

  public void occupyTerminal() {
    this.isAvailable = false;
  }

  public void freeTerminal() {
    this.isAvailable = true;
  }

  public boolean acceptsCars() {
    return acceptsCars;
  }

  public boolean acceptsBuses() {
    return acceptsBuses;
  }

  public boolean acceptsTrucks() {
    return acceptsTrucks;
  }

}
