package net.etfbl.pj2;

import java.util.Random;
import java.util.ArrayList;
import java.util.logging.Level;

public class CustomsTerminal implements BorderTerminal {
  private boolean isAvailable = true;
  private boolean isWorking = true;
  private String name;
  private boolean acceptsCars;
  private boolean acceptsBuses;
  private Boolean acceptsTrucks;

  public CustomsTerminal(String name, boolean acceptsCars, boolean acceptsBuses, boolean acceptsTrucks) {
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

  private boolean checkBusBaggage(Bus vehicle) {
    ArrayList<Suitcase> baggage = vehicle.getBaggage();
    ArrayList<Passenger> problematicPassengers = new ArrayList<>();
    boolean passed;
    for (Suitcase suitcase : baggage) {
      if (suitcase.hasProhibitedItems()) {
        Passenger problematicPassenger = vehicle.getPassengerByID(suitcase.getOwner().getID());
        if (problematicPassenger != null) {
          problematicPassengers.add(problematicPassenger);
          vehicle.removePassenger(vehicle.getPassengerByID(suitcase.getOwner().getID()));
        }
      }
    }

    if (problematicPassengers.size() > 0) {
      if (problematicPassengers.contains(vehicle.getDriver())) {
        System.out.println(name + ": Bus " + vehicle.getID() + " did not pass customs. Problematic passengers were: ");
        passed = false;
      } else {
        System.out.println(name + ": Bus " + vehicle.getID() + " passed customs. Problematic passengers were: ");
        passed = true;
      }
      for (Passenger passenger : problematicPassengers) {
        System.out.println(passenger.getFullName());
      }
      Incident incident = new Incident("customs", problematicPassengers, vehicle.getID(), passed,
          "Invalid items in baggage",
          "Bus", "bus.png");
      FileUtils.writeIncidentToTextFile(incident, "customs_incidents_");
    } else {
      System.out.println(name + ": Bus" + vehicle.getID() + " passed customs without incidents");
      passed = true;
    }
    return passed;
  }

  @Override
  public synchronized boolean processVehicle(Car car) {
    try {
      System.out.println("Processing car on terminal " + name);
      car.checkPause();
      Thread.sleep(2000);
      car.checkPause();
      System.out.println(name + ": Car " + car.getID() + " passed customs without incidents");
    } catch (InterruptedException e) {
      Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing car", e);
    }
    return true;
  }

  public synchronized boolean processVehicle(Bus bus) {
    int timePerPassenger = 100;
    try {
      System.out.println("Processing bus on terminal " + name);
      checkBusBaggage(bus);
      bus.checkPause();
      Thread.sleep(bus.getNumPassengers() * timePerPassenger);
      bus.checkPause();
    } catch (InterruptedException e) {
      Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing bus", e);
    }

    return false;
  }

  public synchronized boolean processVehicle(Truck truck) {
    Random random = new Random();
    System.out.println(name + ": Processing truck.");
    // generate documentation if needed
    boolean documentationGenerated = false;
    if (random.nextDouble(1) <= 0.5) {
      truck.checkPause();
      documentationGenerated = true;
      System.out.println("Generating documentation for truck: " + truck.getID());
      truck.generateDocumentation();
      truck.checkPause();
    }

    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing truck", e);
    }

    if (truck.getRealMass() > truck.getDeclaredMass()) {
      truck.checkPause();
      System.out.println("Checking truck mass: " + truck.getID());
      if (truck.getRealMass() > truck.getDeclaredMass()) {
        Incident incident = new Incident("customs", null, truck.getID(), false,
            "Real mass of the vehicle was greater than declared."
                + (documentationGenerated ? " Documentation had to be generated." : ""),
            truck.getType(), truck.getIconName());
      }
      truck.checkPause();
      return false;
    }
    return true;
  }

  public void changeTerminalStatus(boolean status) {
    isWorking = status;
  }

  public boolean isAvailable() {
    return isAvailable;
  }

  public boolean isWorking() {
    return isWorking;
  }

  public void occupyTerminal() {
    isAvailable = false;
  }

  public void freeTerminal() {
    isAvailable = true;
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
