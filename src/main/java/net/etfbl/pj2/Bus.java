package net.etfbl.pj2;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.ImageView;
import java.util.logging.Level;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class Bus extends PassengerVehicle {
  private int passengerCapacity = 52;
  private ArrayList<Suitcase> baggage;
  private ImageView busIcon;

  public Bus() {
    passengers = PassengerGenerator.generatePassengers(passengerCapacity);
    setDriver();
    baggage = generateBaggage(passengers);
    busIcon = fetchIcon();
  }

  public Bus(Passenger driver, ArrayList<Passenger> passengers, ArrayList<Suitcase> baggage) {
    this.driver = driver;
    this.passengers = passengers;
    this.baggage = baggage;
  }

  public Bus(Passenger driver, Passenger[] passengers, Suitcase[] baggage) {
    this.driver = driver;
    this.passengers = new ArrayList<Passenger>(Arrays.asList(passengers));
    this.baggage = new ArrayList<Suitcase>(Arrays.asList(baggage));
  }

  public ArrayList<Passenger> getPassengers() {
    return passengers;
  }

  public Passenger getPassengerByID(String ID) {
    for (Passenger passenger : this.passengers) {
      if (ID.equals(passenger.getID())) {
        return passenger;
      }
    }
    return null;
  }

  public ArrayList<Suitcase> getBaggage() {
    return baggage;
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
      while (!lockAcquired) {
        checkPause();
        if (!Simulation.C1.isWorking()) {
          try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing bus", e);
          }
        }
        synchronized (Simulation.C1) {
          if (Simulation.C1.isAvailable() && Simulation.C1.acceptsBuses() && Simulation.C1.isWorking()) {
            if (Simulation.lock1.isHeldByCurrentThread()) {
              Main.setP1Vehicle(null);
              Simulation.lock1.unlock();
            } else if (Simulation.lock2.isHeldByCurrentThread()) {
              Main.setP2Vehicle(null);
              Simulation.lock2.unlock();
            }
            Main.setC1Vehicle(this.getIcon());
            lockAcquired = true;
            Simulation.C1.occupyTerminal();
            passedCustoms = Simulation.C1.processVehicle(this);
            checkPause();
            Simulation.C1.freeTerminal();
            Main.setC1Vehicle(null);
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

  @Override
  public ImageView getIcon() {
    return busIcon;
  }

  private ImageView fetchIcon() {
    ImageView vehicleIcon = Main.createImageView("bus.png");
    vehicleIcon.fitHeightProperty().bind(Main.primaryStage.heightProperty().multiply(0.1));
    Bus bus = this;
    vehicleIcon.fitHeightProperty().bind(Main.primaryStage.heightProperty().multiply(0.1));
    vehicleIcon.setOnMouseClicked(new EventHandler<MouseEvent>() {
      @Override
      public void handle(MouseEvent event) {
        Main.showPopUp(bus);
      }
    });
    return vehicleIcon;
  }

  protected void checkPause() {
    synchronized (Main.pauseMonitor) {
      if (Simulation.isPaused) {
        try {
          Main.pauseMonitor.wait();
        } catch (InterruptedException e) {
          Simulation.logger.log(Level.SEVERE, "An interrupted exception happened while processing bus", e);
        }
      }
    }
  }

  private void setDriver() {
    Random random = new Random();
    int randomIndex = random.nextInt(passengers.size());
    driver = passengers.get(randomIndex);
  }

  @Override
  public String getType() {
    return "BUS";
  }

  @Override
  public String getIconName() {
    return "bus.png";
  }
}
