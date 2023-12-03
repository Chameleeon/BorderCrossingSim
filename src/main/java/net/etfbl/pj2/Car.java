package net.etfbl.pj2;

import java.util.logging.Level;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.image.ImageView;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class Car extends PassengerVehicle {
  private int passengerCapacity = 5;
  private ArrayList<Suitcase> baggage;
  private ImageView carIcon;
  private String iconName;

  public Car() {
    passengers = PassengerGenerator.generatePassengers(passengerCapacity);
    setDriver();
    baggage = generateBaggage(passengers);
    carIcon = fetchIcon();
  }

  public Car(Passenger driver, ArrayList<Passenger> passengers, ArrayList<Suitcase> baggage) {
    this.driver = driver;
    this.passengers = passengers;
    this.baggage = baggage;
  }

  public Car(Passenger driver, Passenger[] passengers, Suitcase[] baggage) {
    this.driver = driver;
    this.passengers = new ArrayList<Passenger>(Arrays.asList(passengers));
    this.baggage = new ArrayList<Suitcase>(Arrays.asList(baggage));
  }

  private void setDriver() {
    Random random = new Random();
    int randomIndex = random.nextInt(passengers.size());
    driver = passengers.get(randomIndex);
  }

  @Override
  public void removePassenger(Passenger passenger) {
    passengers.remove(passenger);
  }

  @Override
  public ArrayList<Passenger> getPassengers() {
    return this.passengers;
  }

  public ArrayList<Suitcase> getBaggage() {
    return this.baggage;
  }

  public String getID() {
    return ID;
  }

  @Override
  public Passenger getDriver() {
    return driver;
  }

  @Override
  public void run() {
    boolean lockAcquired = false;
    boolean passedPolice = false;
    while (!lockAcquired) {
      checkPause();
      if (this.ID.equals(Simulation.vehicles.peek().getID())) {
        if (Simulation.P1.isWorking() && Simulation.lock1.tryLock() && Simulation.P1.acceptsBuses()) {
          lockAcquired = true;
          Main.setP1Vehicle(this.getIcon());
          Simulation.P1.occupyTerminal();
          Simulation.vehicles.poll();
          passedPolice = Simulation.P1.processVehicle(this);
          checkPause();
          Simulation.P1.freeTerminal();
        } else if (Simulation.P2.isWorking() && Simulation.lock2.tryLock() && Simulation.P2.acceptsBuses()) {
          lockAcquired = true;
          Main.setP2Vehicle(this.getIcon());
          Simulation.P2.occupyTerminal();
          Simulation.vehicles.poll();
          passedPolice = Simulation.P2.processVehicle(this);
          checkPause();
          Simulation.P2.freeTerminal();
        }
      }
    }
    lockAcquired = false;
    boolean passedCustoms = false;
    if (passedPolice) {
      checkPause();
      while (!lockAcquired) {
        if (!Simulation.C1.isWorking()) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing car", e);
          }
        }
        synchronized (Simulation.C1) {
          if (Simulation.C1.isAvailable() && Simulation.C1.acceptsCars() && Simulation.C1.isWorking()) {

            if (Simulation.lock1.isHeldByCurrentThread()) {
              Simulation.lock1.unlock();
              Main.setP1Vehicle(null);
            } else if (Simulation.lock2.isHeldByCurrentThread()) {
              Main.setP2Vehicle(null);
              Simulation.lock2.unlock();
            }
            lockAcquired = true;
            Main.setC1Vehicle(this.getIcon());
            Simulation.C1.occupyTerminal();
            passedCustoms = Simulation.C1.processVehicle(this);
            checkPause();
            Main.setC1Vehicle(null);
            Simulation.C1.freeTerminal();
          }
        }
      }
    } else {
      System.out.println("C1: did not pass police check");
      if (Simulation.lock1.isHeldByCurrentThread()) {
        Main.setP1Vehicle(null);
        Simulation.lock1.unlock();
      } else if (Simulation.lock2.isHeldByCurrentThread()) {
        Main.setP2Vehicle(null);
        Simulation.lock2.unlock();
      }
    }
    Simulation.processedVehicles++;
  }

  @Override
  public int getNumPassengers() {
    return passengers.size();
  }

  @Override
  public ImageView getIcon() {
    return carIcon;
  }

  protected void checkPause() {
    synchronized (Main.pauseMonitor) {
      if (Simulation.isPaused) {
        try {
          Main.pauseMonitor.wait();
        } catch (InterruptedException e) {
          Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing car", e);
        }
      }
    }
  }

  private ImageView fetchIcon() {
    Random random = new Random();
    int carNum = random.nextInt(3) + 1;
    ImageView vehicleIcon = Main.createImageView("car" + carNum + ".png");
    Car car = this;
    vehicleIcon.fitHeightProperty().bind(Main.primaryStage.heightProperty().multiply(0.1));
    vehicleIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        Main.showPopUp(car);
      }
    });
    iconName = "car" + carNum + ".png";
    return vehicleIcon;
  }

  @Override
  public String getType() {
    return "CAR";
  }

  @Override
  public String getIconName() {
    return iconName;
  }
}
