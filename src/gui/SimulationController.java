package gui;

import city.*;
import file.CityReader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class SimulationController {

    private City city;
    private CityUpdater updater;
    private final int maxLanes = 6;
    private final double step = 10;
    ScheduledExecutorService executor;

    @FXML
    private Canvas simulationView;
    @FXML
    private GridPane simulationGrid;

    public void initialize() {
        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream("src/app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String cityInput = appProps.getProperty("cityPath");
        CityReader reader = new CityReader(cityInput);
        city = reader.readCity();
        draw();
        updater = new CityUpdater(city, this);
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(updater, 0, 35, TimeUnit.MILLISECONDS);
    }

    @FXML
    public void readKeys(KeyEvent e) throws IOException {
        if (city != null) {
            String key = e.getCode().toString();
            switch (key) {
                case "A": {
                    city.moveRight(step);
                    break;
                }
                case "D": {
                    city.moveLeft(step);
                    break;
                }
                case "W": {
                    city.moveDown(step);
                    break;
                }
                case "S": {
                    city.moveUp(step);
                    break;
                }
                case "L": {
                    city.updateCity();
                    break;
                }
                case "K": {
                    city.changeL();
                    break;
                }
                case "C": {
                    city.addRandCar();
                    break;
                }
                case "ENTER": {
                    returnToMainMenu();
                    break;
                }
                case "Q": {
                    System.out.println(city.getCollisions());
                    break;
                }
            }
            draw();
        }
    }

    private void prepareBackground() {
        GraphicsContext gc = simulationView.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 1600, 900);
    }

    public void draw() {
        double drawLen = city.getDrawLen();
        double offset = city.getOffset();
        prepareBackground();
        GraphicsContext gc = simulationView.getGraphicsContext2D();
        drawJunctions(drawLen, gc);
        drawRoads(drawLen, gc);
        drawCars(drawLen, gc, offset);
        drawTimer(gc);
        drawWeather(gc);
    }

    private void drawTimer(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(new Font(35));
        gc.fillText("Elapsed time: " + city.getTimeElapsed(), 10, 25);
        gc.fillText("Cars: " + city.getCurrentCars(), 10, 65);
    }

    private void drawWeather(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        gc.moveTo(20, 80);
        gc.lineTo(110, 80);
        gc.lineTo(110, 170);
        gc.lineTo(20, 170);
        gc.lineTo(20, 80);
        gc.stroke();
        if (city.getWeather() == "rain") {
            gc.setFill(Color.BLUE);
            gc.fillOval(40, 110, 50, 50);
            double[] triX = {41, 89, 65};
            double[] triY = {127.5, 127.5, 80};
            gc.fillPolygon(triX, triY, 3);
        } else if (city.getWeather() == "snow") {
            gc.setStroke(Color.SKYBLUE);
            gc.moveTo(65, 80);
            gc.lineTo(65, 170);
            gc.moveTo(20, 125);
            gc.lineTo(110, 125);
            gc.moveTo(30, 90);
            gc.lineTo(100, 160);
            gc.moveTo(30, 160);
            gc.lineTo(100, 90);
            gc.stroke();
        } else {
            gc.setFill(Color.GOLD);
            gc.fillOval(40, 100, 50, 50);
            gc.setStroke(Color.GOLD);
            gc.moveTo(40, 125);
            gc.lineTo(20, 125);
            gc.moveTo(90, 125);
            gc.lineTo(110, 125);
            gc.moveTo(65, 100);
            gc.lineTo(65, 80);
            gc.moveTo(65, 145);
            gc.lineTo(65, 165);
            gc.moveTo(30, 90);
            gc.lineTo(100, 160);
            gc.moveTo(30, 160);
            gc.lineTo(100, 90);
            gc.stroke();
        }
    }

    private void drawCars(double drawLen, GraphicsContext gc, double cityOffset) {
        double carDrawLen = drawLen / 2;
        for (Road road : city.getRoads()) {
            int side = road.getSide();
            for (Car car : road.getCars()) {
                double offset = cityOffset;
                int lane = car.getLane();
                double laneOffset = lane * 4 * offset;
                int laneNum = road.getLaneNum();
                if (laneNum == 2) {
                    offset *= 2;
                    laneOffset = lane * 3 * offset;
                } else if (laneNum == 1) {
                    offset *= 2;
                }
                double carPosX = 0, carPosY = 0;
                switch (side) {
                    case 0: {
                        carPosX = road.getFrom().getX() + maxLanes * drawLen + car.getCurrentPosition();
                        carPosY = road.getFrom().getY() + (double) maxLanes / 2 * drawLen + offset + laneOffset;
                        break;
                    }
                    case 2: {
                        carPosX = road.getFrom().getX() - car.getCurrentPosition();
                        carPosY = road.getFrom().getY() + (double) maxLanes / 2 * drawLen - carDrawLen - offset - laneOffset;
                        break;
                    }
                    case 1: {
                        carPosX = road.getFrom().getX() + (double) maxLanes / 2 * drawLen - carDrawLen - offset - laneOffset;
                        carPosY = road.getFrom().getY() + maxLanes * drawLen + car.getCurrentPosition();
                        break;
                    }
                    case 3: {
                        carPosX = road.getFrom().getX() + (double) maxLanes / 2 * drawLen + offset + laneOffset;
                        carPosY = road.getFrom().getY() - car.getCurrentPosition();
                        break;
                    }
                }
                gc.beginPath();
                if (car.isDisabled()) {
                    gc.setStroke(Color.BLACK);
                } else if (car.getDriver().getDriverName() == "standard") {
                    gc.setStroke(Color.BLUE);
                } else if (car.getDriver().getDriverName() == "aggressive") {
                    gc.setStroke(Color.CRIMSON);
                } else {
                    gc.setStroke(Color.DARKOLIVEGREEN);
                }
                gc.setLineWidth(1);
                gc.moveTo(carPosX, carPosY);
                if (side == 0) {
                    gc.lineTo(carPosX - 2 * carDrawLen, carPosY);
                    gc.lineTo(carPosX - 2 * carDrawLen, carPosY + carDrawLen);
                    gc.lineTo(carPosX, carPosY + carDrawLen);
                    gc.lineTo(carPosX, carPosY);
                } else if (side == 2) {
                    gc.lineTo(carPosX + 2 * carDrawLen, carPosY);
                    gc.lineTo(carPosX + 2 * carDrawLen, carPosY + carDrawLen);
                    gc.lineTo(carPosX, carPosY + carDrawLen);
                    gc.lineTo(carPosX, carPosY);
                } else if (side == 1) {
                    gc.lineTo(carPosX + carDrawLen, carPosY);
                    gc.lineTo(carPosX + carDrawLen, carPosY - 2 * carDrawLen);
                    gc.lineTo(carPosX, carPosY - 2 * carDrawLen);
                    gc.lineTo(carPosX, carPosY);
                } else {
                    gc.lineTo(carPosX + carDrawLen, carPosY);
                    gc.lineTo(carPosX + carDrawLen, carPosY + 2 * carDrawLen);
                    gc.lineTo(carPosX, carPosY + 2 * carDrawLen);
                    gc.lineTo(carPosX, carPosY);
                }
                gc.stroke();
            }
        }
    }

    private void drawRoads(double drawLen, GraphicsContext gc) {
        for (Road road : city.getRoads()) {
            Junction from = road.getFrom();
            Junction to = road.getTo();
            double tX = to.getX();
            double tY = to.getY();
            double fX = from.getX();
            double fY = from.getY();
            int lanes = road.getLaneNum();
            int side = road.getSide();
            double sX = 0, sY = 0, eX = 0, eY = 0;
            switch (side) {
                case 0: {
                    sX = fX + maxLanes * drawLen;
                    eX = tX;
                    sY = fY + (double) maxLanes * drawLen / 2;
                    eY = tY + (double) maxLanes * drawLen / 2;
                    drawRoadLeftRight(sX, sY, eX, eY, lanes, road.getTraffic(drawLen), gc);
                    break;
                }
                case 2: {
                    sX = fX;
                    eX = tX + maxLanes * drawLen;
                    sY = fY;
                    eY = tY;
                    drawRoadLeftRight(sX, sY, eX, eY, lanes, road.getTraffic(drawLen), gc);
                    break;
                }
                case 1: {
                    sX = fX;
                    eX = tX;
                    sY = fY + maxLanes * drawLen;
                    eY = tY;
                    drawRoadUpDown(sX, sY, eX, eY, lanes, road.getTraffic(drawLen), gc);
                    break;
                }
                case 3: {
                    sX = fX + (double) maxLanes / 2 * drawLen;
                    eX = tX + (double) maxLanes / 2 * drawLen;
                    sY = fY;
                    eY = tY + maxLanes * drawLen;
                    drawRoadUpDown(sX, sY, eX, eY, lanes, road.getTraffic(drawLen), gc);
                    break;
                }
            }
            if (road.getLength() == -1) {
                double len = sqrt(pow((eX - sX), 2) + pow((eY - sY), 2));
                road.setLength(len);
            }
        }
    }

    private void drawJunctions(double drawLen, GraphicsContext gc) {

        for (Junction junction : city.getJunctions()) {
            double x = junction.getX();
            double y = junction.getY();
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);
            gc.beginPath();
            gc.moveTo(x, y);
            gc.lineTo(x + maxLanes * drawLen, y);
            gc.lineTo(x + maxLanes * drawLen, y + maxLanes * drawLen);
            gc.lineTo(x, y + maxLanes * drawLen);
            gc.lineTo(x, y);
            gc.stroke();

            gc.setFill(Color.BLACK);
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setFont(new Font(15));
            gc.fillText(String.valueOf(junction.getId()), x + 1, y + 13);

            gc.beginPath();
            gc.setStroke(Color.GREEN);
            gc.setLineWidth(5);
            if (junction.checkForGreenLight(0)) {
                gc.moveTo(x, y + maxLanes * drawLen / 2);
                gc.lineTo(x + maxLanes * drawLen, y + maxLanes * drawLen / 2);
                gc.stroke();
                gc.beginPath();
                gc.setStroke(Color.RED);
                gc.moveTo(x + maxLanes * drawLen / 2, y);
                gc.lineTo(x + maxLanes * drawLen / 2, y + maxLanes * drawLen);
            } else {
                gc.moveTo(x + maxLanes * drawLen / 2, y);
                gc.lineTo(x + maxLanes * drawLen / 2, y + maxLanes * drawLen);
                gc.stroke();
                gc.beginPath();
                gc.setStroke(Color.RED);
                gc.moveTo(x, y + maxLanes * drawLen / 2);
                gc.lineTo(x + maxLanes * drawLen, y + maxLanes * drawLen / 2);
            }
            gc.stroke();
        }

    }

    private void drawRoadLeftRight(double sX, double sY, double eX, double eY, int lanes, double traffic, GraphicsContext gc) {
        double drawLen = city.getDrawLen();
        addTrafficColor(traffic, sX, sY, eX, eY, gc, 0);
        gc.beginPath();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.moveTo(sX, sY);
        gc.lineTo(eX, eY);
        gc.moveTo(sX, sY + (double) maxLanes / 2 * drawLen);
        gc.lineTo(eX, eY + (double) maxLanes / 2 * drawLen);
        gc.stroke();
        if (lanes == 2) {
            gc.beginPath();
            gc.setStroke(Color.DARKGOLDENROD);
            gc.setLineWidth(0.5);
            gc.moveTo(sX, sY + (double) maxLanes / 4 * drawLen);
            gc.lineTo(eX, eY + (double) maxLanes / 4 * drawLen);
            gc.stroke();
        } else if (lanes == 3) {
            gc.beginPath();
            gc.setStroke(Color.DARKGOLDENROD);
            gc.setLineWidth(0.5);
            gc.moveTo(sX, sY + (double) maxLanes / 3 * drawLen);
            gc.lineTo(eX, eY + (double) maxLanes / 3 * drawLen);
            gc.moveTo(sX, sY + drawLen);
            gc.lineTo(eX, eY + drawLen);
            gc.stroke();
        }
    }

    private void drawRoadUpDown(double sX, double sY, double eX, double eY, int lanes, double traffic, GraphicsContext gc) {
        double drawLen = city.getDrawLen();
        addTrafficColor(traffic, sX, sY, eX, eY, gc, 1);
        gc.beginPath();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.moveTo(sX, sY);
        gc.lineTo(eX, eY);
        gc.moveTo(sX + (double) maxLanes / 2 * drawLen, sY);
        gc.lineTo(eX + (double) maxLanes / 2 * drawLen, eY);
        gc.stroke();
        if (lanes == 2) {
            gc.beginPath();
            gc.setStroke(Color.DARKGOLDENROD);
            gc.setLineWidth(0.5);
            gc.moveTo(sX + (double) maxLanes / 4 * drawLen, sY);
            gc.lineTo(eX + (double) maxLanes / 4 * drawLen, eY);
            gc.stroke();
        } else if (lanes == 3) {
            gc.beginPath();
            gc.setStroke(Color.DARKGOLDENROD);
            gc.setLineWidth(0.5);
            gc.moveTo(sX + (double) maxLanes / 3 * drawLen, sY);
            gc.lineTo(eX + (double) maxLanes / 3 * drawLen, eY);
            gc.moveTo(sX + drawLen, sY);
            gc.lineTo(eX + drawLen, eY);
            gc.stroke();
        }
    }

    private void addTrafficColor(double traffic, double x1, double y1, double x2, double y2, GraphicsContext gc, int side){
        if(traffic > 60.0){
            gc.setFill(Color.rgb(162,0,0,0.15));
        } else if(traffic > 40.0){
            gc.setFill(Color.rgb(255, 128, 0, 0.15));
        } else if(traffic > 20.0){
            gc.setFill(Color.rgb(255,255,0,0.15));
        } else {
            return;
        }
        double rectStartX = Math.min(x1,x2);
        double rectStartY = Math.min(y1,y2);
        double rectEndX = Math.max(x1,x2);
        double rectEndY = Math.max(y1,y2);
        double xLen;
        double yLen;
        if( side == 0 ){
            xLen = rectEndX-rectStartX;
            yLen = city.getDrawLen() * 3;
        } else {
            xLen = city.getDrawLen() * 3;
            yLen = rectEndY-rectStartY;
        }
        gc.fillRect(rectStartX, rectStartY, xLen, yLen);
    }


    public void stopRunning() {
        executor.shutdownNow();
        Label simEndedNotification = new Label("Simulation finished! Press ENTER to return to main menu.");
        simEndedNotification.setFont(new Font(40));
        GridPane.setHalignment(simEndedNotification, HPos.CENTER);
        simulationGrid.add(simEndedNotification, 0, 0);
    }

    private void returnToMainMenu() throws IOException {
        stopRunning();
        Stage stage = (Stage) simulationGrid.getScene().getWindow();
        Parent settings = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));
        stage.setScene(new Scene(settings, 1024, 800));
        stage.centerOnScreen();
    }
}
