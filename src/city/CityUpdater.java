package city;

import gui.SimulationController;
import javafx.application.Platform;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class CityUpdater implements Runnable {
    private final City city;
    private final SimulationController ctrl;

    public CityUpdater(City city, SimulationController ctrl) {
        this.city = city;
        this.ctrl = ctrl;
    }

    @Override
    public void run() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(Double.parseDouble(city.getTimeElapsed()) >= city.getSimulationTime()){
                    ctrl.stopRunning();
                    generateSimulationReport();
                }
                if(0.65 * city.getMaxAllowedCars() > city.getCurrentCars()){
                    city.addRandCar();
                    city.addRandCar();
                    city.addRandCar();
                }
                else if(0.95 * city.getMaxAllowedCars() > city.getCurrentCars()){
                    city.addRandCar();
                    city.addRandCar();
                } else if(city.getMaxAllowedCars() > city.getCurrentCars()){
                    city.addRandCar();
                }
                city.updateCity();
                ctrl.draw();
            }
        });
    }

    private void generateSimulationReport() {
        String report;
        report = "SIMULATION REPORT\n";
        //Simulation configuration
        report += "\nSimulation configuration:";
        report += "\nMax number of cars = " + city.getMaxAllowedCars();
        report += "\nSimulation time = " + city.getSimulationTime();
        report += "\nWeather = " + city.getWeather();
        report += "\nDriver mods for careful/standard/aggressive = " + city.getCarefulDriverMod() + "/" + city.getStandardDriverMod() + "/" + city.getAggressiveDriverMod();
        report += "\nRouter type = " + city.getRouterType();
        report += "\n\n##################################\n";
        //Road statistics
        for (Road road : city.getRoads()) {
            report += "\n" + road.getStatistics();
        }
        //Save report to file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(city.getReportOutput()));
            writer.write(report);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
