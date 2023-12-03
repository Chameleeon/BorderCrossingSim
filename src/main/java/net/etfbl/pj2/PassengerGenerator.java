package net.etfbl.pj2;

import java.util.Random;
import java.util.ArrayList;
import java.io.*;
import java.util.logging.Level;

public class PassengerGenerator {
  private static final double invalidDocumentProbability = 0.03;

  private PassengerGenerator() {
  }

  public static ArrayList<Passenger> generatePassengers(int maxCapacity) {
    Random random = new Random();
    int numPassengers = random.nextInt(maxCapacity) + 1;
    ArrayList<Passenger> passengers = new ArrayList<>();

    for (int i = 0; i < numPassengers; i++) {
      String firstName = getRandomName("first_names.txt");
      String lastName = getRandomName("last_names.txt");
      Passenger passenger = new Passenger(firstName, lastName);
      passenger.setDocument(DocumentGenerator.generateDocument(passenger.getFirstName(), passenger.getLastName(),
          invalidDocumentProbability));
      passengers.add(passenger);
    }
    return passengers;
  }

  private static String getRandomName(String fileName) {
    String file = "data" + File.separator + "passenger_data" + File.separator + fileName;
    ArrayList<String> names = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = br.readLine()) != null) {
        names.add(line);
      }
    } catch (IOException e) {
      Simulation.logger.log(Level.SEVERE, "Data read was not a number.", e);
    }

    Random random = new Random();
    int randomIndex = random.nextInt(names.size());
    return names.get(randomIndex);
  }
}
