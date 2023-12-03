package net.etfbl.pj2;

import javafx.scene.image.ImageView;
import java.util.logging.Level;
import java.util.Random;
import java.util.Arrays;
import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class Truck extends PassengerVehicle {
  private int passengerCapacity = 3;
  private boolean hasDocumentation = false;
  private int realMass;
  private int declaredMass;
  private ImageView truckIcon;
  private String iconName;

  public Truck() {
    passengers = PassengerGenerator.generatePassengers(passengerCapacity);
    setDriver();
    generateMass();
    truckIcon = fetchIcon();
  }

  public Truck(Passenger driver, ArrayList<Passenger> passengers, ArrayList<Suitcase> baggage) {
    this.driver = driver;
    this.passengers = passengers;
    generateMass();
  }

  public Truck(Passenger driver, Passenger[] passengers, Suitcase[] baggage) {
    this.driver = driver;
    this.passengers = new ArrayList<Passenger>(Arrays.asList(passengers));
    generateMass();
  }

  public ArrayList<Passenger> getPassengers() {
    return passengers;
  }

  @Override
  public void removePassenger(Passenger passenger) {
    passengers.remove(passenger);
  }

  @Override
  public void run() {
    boolean lockAcquired = false;
    boolean passedPolice = false;
    while (!lockAcquired) {
      checkPause();
      if (this.ID.equals(Simulation.vehicles.peek().getID()) && Simulation.P3.isAvailable()) {
        if (Simulation.P3.isWorking()) {
          synchronized (Simulation.P3) {
            lockAcquired = true;
            Main.setP3Vehicle(this.getIcon());
            Simulation.P3.occupyTerminal();
            Simulation.vehicles.poll();
            passedPolice = Simulation.P3.processVehicle(this);
          }
        }
      }
    }
    lockAcquired = false;
    if (passedPolice) {
      while (!lockAcquired) {
        checkPause();
        synchronized (Simulation.C2) {
          if (Simulation.C2.isWorking() && Simulation.C2.isAvailable()) {
            Simulation.P3.freeTerminal();
            Main.setP3Vehicle(null);
            lockAcquired = true;
            Main.setC2Vehicle(this.getIcon());
            Simulation.C2.occupyTerminal();
            Simulation.C2.processVehicle(this);
            checkPause();
            Simulation.C2.freeTerminal();
            Main.setC2Vehicle(null);
          }
        }
      }
    } else {
      System.out.println("C2: Truck did not pass police check.");
      Simulation.P3.freeTerminal();
      Main.setC2Vehicle(null);
    }
    Simulation.processedVehicles++;
  }

  public String getID() {
    return ID;
  }

  @Override
  public Passenger getDriver() {
    return driver;
  }

  @Override
  public int getNumPassengers() {
    return passengers.size();
  }

  public void generateDocumentation() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Simulation.logger.log(Level.SEVERE,
          "An interrupted exception happened while generating documentation for a truck", e);
    }
  }

  public boolean hasDocumentation() {
    return hasDocumentation;
  }

  public int getRealMass() {
    return realMass;
  }

  public int getDeclaredMass() {
    return declaredMass;
  }

  private void setDriver() {
    Random random = new Random();
    int randomIndex = random.nextInt(passengers.size());
    driver = passengers.get(randomIndex);
  }

  private void generateMass() {
    double higherRealMassProbability = 0.2;
    Random random = new Random();
    declaredMass = random.nextInt(9001) + 1000;
    double multiplier = 1.0 + random.nextDouble(0.3);

    if (random.nextDouble() <= higherRealMassProbability) {
      realMass = (int) (declaredMass * multiplier);
    } else {
      realMass = declaredMass;
    }
  }

  @Override
  public ImageView getIcon() {
    return truckIcon;
  }

  protected void checkPause() {
    synchronized (Main.pauseMonitor) {
      if (Simulation.isPaused) {
        try {
          Main.pauseMonitor.wait();
        } catch (InterruptedException e) {
          Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing truck", e);
        }
      }
    }

  }

  private ImageView fetchIcon() {
    Random random = new Random();
    int carNum = random.nextInt(2) + 1;
    ImageView vehicleIcon = Main.createImageView("truck" + carNum + ".png");
    vehicleIcon.fitHeightProperty().bind(Main.primaryStage.heightProperty().multiply(0.1));
    Truck truck = this;
    vehicleIcon.fitHeightProperty().bind(Main.primaryStage.heightProperty().multiply(0.1));
    vehicleIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        Main.showPopUp(truck);
      }
    });
    iconName = "truck" + carNum + ".png";
    return vehicleIcon;
  }

  public String getIconName() {
    return iconName;
  }

  @Override
  public String getType() {
    return "TRUCK";
  }
}
