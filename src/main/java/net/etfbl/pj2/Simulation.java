package net.etfbl.pj2;

import javafx.application.Platform;
import java.util.Date;
import java.util.Collections;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.ArrayBlockingQueue;
import javafx.scene.Scene;

public class Simulation {
  private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
  protected static Logger logger = Logger.getLogger(Simulation.class.getName());
  protected static PoliceTerminal P1 = new PoliceTerminal("P1", true, true, false);
  protected static PoliceTerminal P2 = new PoliceTerminal("P2", true, true, false);
  protected static PoliceTerminal P3 = new PoliceTerminal("P3", false, false, true);
  protected static CustomsTerminal C1 = new CustomsTerminal("C1", true, true, false);
  protected static CustomsTerminal C2 = new CustomsTerminal("C2", false, false, true);
  protected static ArrayBlockingQueue<PassengerVehicle> vehicles;
  protected static String date;
  protected static boolean vehiclesCreatedFlag = false;
  protected static int processedVehicles = 0;
  protected static boolean isPaused = false;

  protected static ReentrantLock lock1 = new ReentrantLock();
  protected static ReentrantLock lock2 = new ReentrantLock();
  protected static ReentrantLock lockC1 = new ReentrantLock();

  public static void runSimulation() {
    Thread terminalWatcher = new TerminalWatcher();
    terminalWatcher.setDaemon(true);
    terminalWatcher.start();
    date = Simulation.dateFormat.format(new Date());
    logger = LoggerCustomizer.configureLogger(logger, "simulation_log");

    vehicles = new ArrayBlockingQueue<PassengerVehicle>(50);

    ArrayList<PassengerVehicle> tmpVehicles = new ArrayList<>();

    for (int i = 0; i < 35; i++) {
      Car car = new Car();
      tmpVehicles.add(car);
    }

    for (int i = 0; i < 10; i++) {
      Truck truck = new Truck();
      tmpVehicles.add(truck);
    }

    for (int i = 0; i < 5; i++) {
      Bus bus = new Bus();
      tmpVehicles.add(bus);
    }

    Collections.shuffle(tmpVehicles);

    vehicles.clear();
    vehicles.addAll(tmpVehicles);
    vehiclesCreatedFlag = true;

    for (PassengerVehicle vehicle : vehicles) {
      vehicle.start();
    }
  }
}
