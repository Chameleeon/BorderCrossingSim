package net.etfbl.pj2;

import java.io.Serializable;

public class Suitcase implements Serializable {
  private Document owner;
  private boolean hasProhibitedItems;

  public Suitcase(Document owner, boolean hasProhibitedItems) {
    this.owner = owner;
    this.hasProhibitedItems = hasProhibitedItems;
  }

  public boolean hasProhibitedItems() {
    return hasProhibitedItems;
  }

  public Document getOwner() {
    return owner;
  }
}
