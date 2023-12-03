package net.etfbl.pj2;

import java.util.logging.Level;
import java.io.*;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.image.ImageView;

public class TerminalWatcher extends Thread {
  public TerminalWatcher() {
  }

  protected static Map<String, Boolean> terminalStatus = new HashMap<>();

  static {
    String filePath = "data" + File.separator + "terminal_status.txt";
    File file = new File(filePath);
    if (!file.exists()) {
      try (FileWriter fileWriter = new FileWriter(filePath);
          BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
        for (int i = 1; i <= 3; i++) {
          bufferedWriter.write("P" + i + ":1");
          bufferedWriter.newLine();
        }
        for (int i = 1; i <= 2; i++) {
          bufferedWriter.write("C" + i + ":1");
          bufferedWriter.newLine();
        }
      } catch (IOException e) {
        Simulation.logger.log(Level.SEVERE, "An error occurred while writing to file: " + filePath.toString(), e);
      }
    } else {
      readStatus();
    }
  }

  private static void readStatus() {
    String filePath = "data" + File.separator + "terminal_status.txt";
    try (FileReader fileReader = new FileReader(filePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        String[] parts = line.split(":");
        if (parts.length == 2) {
          String terminalName = parts[0].trim();
          int status = Integer.parseInt(parts[1].trim());
          boolean working = status == 1 ? true : false;
          terminalStatus.put(terminalName, working);
        }
      }
    } catch (IOException e) {
      Simulation.logger.log(Level.SEVERE, "An error occurred while reading file: " + filePath.toString(), e);
    }
  }

  private static void updateStatus() {
    Simulation.P1.changeTerminalStatus(terminalStatus.get("P1"));
    Main.P1 = terminalStatus.get("P1") == true ? Main.activeP1 : Main.inactiveP1;
    Simulation.P2.changeTerminalStatus(terminalStatus.get("P2"));
    Main.P2 = terminalStatus.get("P2") == true ? Main.activeP2 : Main.inactiveP2;
    Simulation.P3.changeTerminalStatus(terminalStatus.get("P3"));
    Main.P3 = terminalStatus.get("P3") == true ? Main.activeP3 : Main.inactiveP3;
    Simulation.C1.changeTerminalStatus(terminalStatus.get("C1"));
    Main.C1 = terminalStatus.get("C1") == true ? Main.activeC1 : Main.inactiveC1;
    Simulation.C2.changeTerminalStatus(terminalStatus.get("C2"));
    Main.C2 = terminalStatus.get("C2") == true ? Main.activeC2 : Main.inactiveC2;
  }

  @Override
  public void run() {
    while (true) {
      readStatus();
      updateStatus();
      try {
        sleep(200);
      } catch (InterruptedException e) {
        Simulation.logger.log(Level.SEVERE, "Interrupted error while reading status", e);
      }
    }
  }
}
