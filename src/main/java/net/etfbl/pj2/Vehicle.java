package net.etfbl.pj2;

import java.io.Serializable;
import java.util.ArrayList;
import javafx.scene.image.ImageView;

public interface Vehicle extends Serializable {

    String getID();

    int getNumPassengers();

    ArrayList<Passenger> getPassengers();

    Passenger getDriver();

    void removePassenger(Passenger passenger);

    ImageView getIcon();

    String getType();
}
