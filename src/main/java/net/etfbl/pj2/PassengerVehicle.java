package net.etfbl.pj2;

import java.util.ArrayList;
import java.util.Random;

public abstract class PassengerVehicle extends Thread implements Vehicle {
  protected static int idCount = 0;
  protected ArrayList<Passenger> passengers;
  protected String ID;
  protected Passenger driver;

  public PassengerVehicle() {
    ID = "V" + idCount++;
  }

  @Override
  public String toString() {
    String vehicle = "Passengers:\n";
    for (Passenger passenger : passengers) {
      vehicle += (passenger + "\n");
    }
    vehicle += "ID: " + ID + "\nDriver: " + driver;

    return vehicle;
  }

  protected ArrayList<Suitcase> generateBaggage(ArrayList<Passenger> passengers) {
    Random random = new Random();
    double generationProbability = 0.7;
    double prohibitedItemProbability = 0.1;
    ArrayList<Suitcase> baggage = new ArrayList<>();
    for (Passenger passenger : passengers) {
      if (random.nextDouble(1) <= generationProbability) {
        Suitcase suitcase = new Suitcase(passenger.getDocument(), random.nextDouble(1) <= prohibitedItemProbability);
        baggage.add(suitcase);
      }
    }
    return baggage;
  }

  public abstract String getIconName();
}
