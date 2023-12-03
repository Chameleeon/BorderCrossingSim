package net.etfbl.pj2;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

public class DocumentGenerator {
  private static int numOfDocuments;

  private DocumentGenerator() {

  }

  public static void generateDocuments(double invalidDocumentProbability, ArrayList<Passenger> passengers) {
    Random random = new Random();
    Document doc = null;
    getCurrentIDCount();

    for (Passenger passenger : passengers) {
      double randomValue = random.nextDouble();
      if (randomValue <= invalidDocumentProbability) {
        doc = new Document(passenger.getFirstName(), passenger.getLastName(), "P" + numOfDocuments++, false);
      } else {
        doc = new Document(passenger.getFirstName(), passenger.getLastName(), "P" + numOfDocuments++, true);
      }
      passenger.setDocument(doc);
    }
    FileUtils.writeIntToBinaryFile(numOfDocuments,
        new File("data" + File.separator + "document_data" + File.separator + "doc_count.bin"));
  }

  public static Document generateDocument(String firstName, String lastName, double invalidDocumentProbability) {
    getCurrentIDCount();
    Random random = new Random();
    double randomValue = random.nextDouble();
    Document doc = null;
    if (randomValue <= invalidDocumentProbability) {
      doc = new Document(firstName, lastName, "P" + numOfDocuments++, false);
    } else {
      doc = new Document(firstName, lastName, "P" + numOfDocuments++, true);
    }
    FileUtils.writeIntToBinaryFile(numOfDocuments,
        new File("data" + File.separator + "document_data" + File.separator + "doc_count.bin"));
    return doc;
  }

  private static synchronized void getCurrentIDCount() {
    try {
      String path = "data" + File.separator + "document_data";
      File file = new File(path);
      if (!file.exists()) {
        file.mkdirs();
        file = new File(path + File.separator + "doc_count.bin");
        try (FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos)) {
          dos.writeInt(0);
          numOfDocuments = 0;
        } catch (IOException e) {
          Simulation.logger.log(Level.SEVERE, "Error writing to file!", e);
        }
      } else {
        file = new File(path + File.separator + "doc_count.bin");
        numOfDocuments = FileUtils.readFirstLine(file);
      }
    } catch (NumberFormatException e) {
      Simulation.logger.log(Level.SEVERE, "Data read was not a number.", e);
    }
  }
}
