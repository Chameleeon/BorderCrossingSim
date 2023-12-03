package net.etfbl.pj2;

import java.io.File;
import java.util.logging.SimpleFormatter;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.io.IOException;

public class LoggerCustomizer {
  private LoggerCustomizer() {
  }

  public static Logger configureLogger(Logger logger, String logFileName) {
    try {
      File logsDir = new File("log");
      if (!logsDir.exists()) {
        logsDir.mkdirs();
      }

      String logFileNameWithDate = logFileName + Simulation.date + ".log";
      FileHandler fileHandler = new FileHandler("log" + File.separator + logFileNameWithDate);

      SimpleFormatter formatter = new SimpleFormatter();
      fileHandler.setFormatter(formatter);

      logger.addHandler(fileHandler);
    } catch (IOException e) {
      System.out.println(e);
    }
    return logger;
  }
}
