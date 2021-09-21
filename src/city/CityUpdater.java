package city;

import gui.Controller;
import javafx.application.Platform;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CityUpdater implements Runnable {
    private final City city;
    private final Controller ctrl;

    public CityUpdater(City city, Controller ctrl) {
        this.city = city;
        this.ctrl = ctrl;
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(city.getMaxAllowedCars() > city.getCurrentCars()){
                    city.addRandCar();
                }
                city.updateCity();
                ctrl.draw();
            }
        });
    }

}
