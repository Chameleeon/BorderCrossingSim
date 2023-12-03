package net.etfbl.pj2;

import java.io.*;

public class AppendingObjectOutputStream extends ObjectOutputStream {
  public AppendingObjectOutputStream(OutputStream out) throws IOException {
    super(out);
  }

  @Override
  protected void writeStreamHeader() throws IOException {
    reset();
  }
}
