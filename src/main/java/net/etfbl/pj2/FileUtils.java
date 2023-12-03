package net.etfbl.pj2;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;
import java.util.logging.Level;

public class FileUtils {

  private FileUtils() {
  }

  public static int readFirstLine(File filePath) {
    try (FileInputStream fileInputStream = new FileInputStream(filePath);
        DataInputStream dataInputStream = new DataInputStream(fileInputStream)) {
      return dataInputStream.readInt();
    } catch (IOException e) {
      Simulation.logger.log(Level.SEVERE, "An error occurred while reading file: " + filePath.toString(), e);
    }
    return 0;
  }

  public synchronized static void writeIntToBinaryFile(int value, File filePath) {
    try (FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream)) {
      dataOutputStream.writeInt(value);
    } catch (IOException e) {
      Simulation.logger.log(Level.SEVERE, "An error occurred while writing to file: " + filePath.toString(), e);
    }
  }

  public synchronized static ArrayList<Incident> readIncidentsFromTextFile() {
    ArrayList<Incident> incidents = new ArrayList<>();
    File file = new File("output" + File.separator + "customs_incidents" + File.separator + "customs_incidents_"
        + Simulation.date + ".txt");
    if (!file.exists()) {
      return null;
    }
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        Incident incident = parseIncident(line);
        if (incident != null) {
          incidents.add(incident);
        }
      }
    } catch (IOException e) {
      Simulation.logger.log(Level.SEVERE, "An error occurred while reading from file: " + file.toString(), e);
    }
    return incidents;
  }

  private static Incident parseIncident(String line) {
    String[] parts = line.split(":");
    if (parts.length < 4) {
      return null;
    }

    String incidentType = parts[0];
    String icon = parts[1];
    String vehicleType = parts[2];
    String vehicldeID = parts[3];
    String description = parts[4];
    boolean vehiclePassed = Boolean.parseBoolean(parts[5]);

    ArrayList<Passenger> problematicPassengers = new ArrayList<>();
    for (int i = 5; i < parts.length; i++) {
      String[] passengerInfo = parts[i].split("_");
      if (passengerInfo.length != 3) {
        continue;
      }
      String passengerID = passengerInfo[0];
      String firstName = passengerInfo[1];
      String lastName = passengerInfo[2];
      problematicPassengers.add(new Passenger(firstName, lastName, passengerID));
    }
    return new Incident(incidentType, problematicPassengers, vehicldeID, vehiclePassed, description, vehicleType, icon);
  }

  public synchronized static void writeIncidentToTextFile(Incident incident, String fileName) {
    String path = "output" + File.separator + "customs_incidents";
    File filePath = new File(path);
    if (!filePath.exists()) {
      filePath.mkdirs();
    }
    fileName = fileName + Simulation.date + ".txt";
    filePath = new File(path + File.separator + fileName);
    try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath, true))) {
      bufferedWriter.write(incident.toString());
      bufferedWriter.newLine();
    } catch (IOException e) {
      Simulation.logger.log(Level.SEVERE, "An error occurred while writing to file: " + filePath.toString(), e);
    }
  }

  public synchronized static void serializeIncident(Object obj, String fileName) {
    String path = "output" + File.separator + "document_incidents";
    File filePath = new File(path);
    if (!filePath.exists()) {
      filePath.mkdirs();
    }
    fileName = fileName + Simulation.date + ".bin";
    filePath = new File(path + File.separator + fileName);
    if (filePath.exists()) {
      try (FileOutputStream fos = new FileOutputStream(filePath, true);
          AppendingObjectOutputStream oos = new AppendingObjectOutputStream(fos)) {
        oos.writeObject(obj);
      } catch (IOException e) {
        Simulation.logger.log(Level.SEVERE, "An error occurred while writing to file: " + filePath.toString(), e);
      }
    } else {
      try (FileOutputStream fos = new FileOutputStream(filePath);
          ObjectOutputStream oos = new ObjectOutputStream(fos)) {
        oos.writeObject(obj);
      } catch (IOException e) {
        Simulation.logger.log(Level.SEVERE, "An error occurred while writing to file: " + filePath.toString(), e);
      }
    }
  }

  public synchronized static ArrayList<Incident> deserializeIncidents(String fileName) {
    String filePath = "output" + File.separator + "document_incidents" + File.separator + fileName
        + Simulation.date + ".bin";
    File file = new File(filePath);
    if (!file.exists()) {
      return null;
    }
    ArrayList<Incident> incidents = new ArrayList<>();
    try (FileInputStream fis = new FileInputStream(filePath);
        ObjectInputStream ois = new ObjectInputStream(fis)) {
      try {
        while (true) {
          Object obj = ois.readObject();

          if (obj instanceof Incident) {
            incidents.add((Incident) obj);
          }
        }
      } catch (EOFException eof) {
      }
    } catch (IOException | ClassNotFoundException e) {
      Simulation.logger.log(Level.SEVERE, "An error occurred while reading file: " + filePath.toString(), e);
    }
    return incidents;
  }

  public static HashMap<String, ArrayList<Incident>> getAllIncidents() {
    HashMap<String, ArrayList<Incident>> incidents = new HashMap<>();
    ArrayList<Incident> policeIncidents = deserializeIncidents("document_incidents_");
    if (policeIncidents != null) {
      for (Incident incident : policeIncidents) {
        if (!incidents.containsKey(incident.getID())) {
          incidents.put(incident.getID(), new ArrayList<Incident>());
        }
        incidents.get(incident.getID()).add(incident);
      }
    }

    ArrayList<Incident> customsIncidents = readIncidentsFromTextFile();
    if (customsIncidents != null) {
      for (Incident incident : customsIncidents) {
        if (!incidents.containsKey(incident.getID())) {
          incidents.put(incident.getID(), new ArrayList<Incident>());
        }
        incidents.get(incident.getID()).add(incident);
      }
    }
    return incidents;
  }
}
