package gui;

import city.Car;
import city.City;
import city.Junction;
import city.Road;
import file.CityReader;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyEvent;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


public class Controller {

    private City city;
    private final int maxLanes = 6;
    private final double step = 10;

    @FXML
    private Canvas simulationView;

    public void initialize() {
        CityReader reader = new CityReader("src/city.txt");
        city = reader.readCity();
        draw();
    }

    @FXML
    public void readKeys(KeyEvent e) {
        if (city != null) {
            String key = e.getCode().toString();
            switch (key) {
                case "A" -> city.moveRight(step);
                case "D" -> city.moveLeft(step);
                case "W" -> city.moveDown(step);
                case "S" -> city.moveUp(step);
                case "L" -> city.updateCity();
//                case "K" -> city.changeL();
//                case "O" -> city.zoom(1);
//                case "P" -> city.zoom(-1);
            }
            draw();
        }
    }

    private void prepareBackground() {
        GraphicsContext gc = simulationView.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, 800, 800);
    }

    private void draw() {
        double drawLen = city.getDrawLen();
        double offset = city.getOffset();
        prepareBackground();
        GraphicsContext gc = simulationView.getGraphicsContext2D();
        drawJunctions(drawLen, gc);
        drawRoads(drawLen, gc);
        drawCars(drawLen, gc, offset);
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
                    case 0 -> {
                        carPosX = road.getFrom().getX() + maxLanes * drawLen + car.getCurrentPosition();
                        carPosY = road.getFrom().getY() + (double) maxLanes / 2 * drawLen + offset + laneOffset;
                    }
                    case 2 -> {
                        carPosX = road.getFrom().getX() - 2 * carDrawLen - car.getCurrentPosition();
                        carPosY = road.getFrom().getY() + (double) maxLanes / 2 * drawLen - carDrawLen - offset - laneOffset;
                    }
                    case 1 -> {
                        carPosX = road.getFrom().getX() + (double) maxLanes / 2 * drawLen - carDrawLen - offset - laneOffset;
                        carPosY = road.getFrom().getY() + maxLanes * drawLen + car.getCurrentPosition();
                    }
                    case 3 -> {
                        carPosX = road.getFrom().getX() + (double) maxLanes / 2 * drawLen + offset + laneOffset;
                        carPosY = road.getFrom().getY() - 2 * carDrawLen - car.getCurrentPosition();
                    }
                }
                gc.beginPath();
                gc.setStroke(Color.BLUE);
                gc.setLineWidth(1);
                gc.moveTo(carPosX, carPosY);
                if (side == 0 || side == 2) {
                    gc.lineTo(carPosX + 2 * carDrawLen, carPosY);
                    gc.lineTo(carPosX + 2 * carDrawLen, carPosY + carDrawLen);
                    gc.lineTo(carPosX, carPosY + carDrawLen);
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
                case 0 -> {
                    sX = fX + maxLanes * drawLen;
                    eX = tX;
                    sY = fY + (double) maxLanes * drawLen / 2;
                    eY = tY + (double) maxLanes * drawLen / 2;
                    drawRoadLeftRight(sX, sY, eX, eY, lanes, gc);
                }
                case 2 -> {
                    sX = fX;
                    eX = tX + maxLanes * drawLen;
                    sY = fY;
                    eY = tY;
                    drawRoadLeftRight(sX, sY, eX, eY, lanes, gc);
                }
                case 1 -> {
                    sX = fX;
                    eX = tX;
                    sY = fY + maxLanes * drawLen;
                    eY = tY;
                    drawRoadUpDown(sX, sY, eX, eY, lanes, gc);
                }
                case 3 -> {
                    sX = fX + (double) maxLanes / 2 * drawLen;
                    eX = tX + (double) maxLanes / 2 * drawLen;
                    sY = fY;
                    eY = tY + maxLanes * drawLen;
                    drawRoadUpDown(sX, sY, eX, eY, lanes, gc);
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

    private void drawRoadLeftRight(double sX, double sY, double eX, double eY, int lanes, GraphicsContext gc) {
        double drawLen = city.getDrawLen();
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

    private void drawRoadUpDown(double sX, double sY, double eX, double eY, int lanes, GraphicsContext gc) {
        double drawLen = city.getDrawLen();
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
}
