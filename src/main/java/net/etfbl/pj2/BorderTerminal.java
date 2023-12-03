package net.etfbl.pj2;

interface BorderTerminal {

  // returns true if the vehicle is allowed to move on
  // otherwise return false
  boolean processVehicle(Car car);

  boolean processVehicle(Bus bus);

  boolean processVehicle(Truck truck);

  boolean isAvailable();

}
