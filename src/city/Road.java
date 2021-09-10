package city;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class Road {
    private final Junction to, from;
    private double length;
    private final int speedLimit;
    private final int laneNum;
    private ArrayList<Car> cars;

    public Road(Junction to, Junction from, int speedLimit, int laneNum) {
        this.to = to;
        this.from = from;
        this.speedLimit = speedLimit;
        this.laneNum = laneNum;
        length = -1;
        cars = new ArrayList<>();
    }

    @Override
    public String toString(){
        return String.format(from.getId() + " -> " + to.getId() + ", length: " + length);
    }

    public Junction getTo() {
        return to;
    }

    public Junction getFrom() {
        return from;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public int getLaneNum() {
        return laneNum;
    }

//    @Override
//    public String toString() {
//        return "Road{" +
//                "to=" + to +
//                ", from=" + from +
//                ", speedLimit=" + speedLimit +
//                '}';
//    }

    public int getSide() {
        double tX = to.getX();
        double tY = to.getY();
        double fX = from.getX();
        double fY = from.getY();
        double xDiff = fX - tX;
        double yDiff = fY - tY;
        if (abs(xDiff) > abs(yDiff)) {
            if (xDiff < 0) {
                return 0;
            } else {
                return 2;
            }
        } else {
            if (yDiff < 0) {
                return 1;
            } else {
                return 3;
            }
        }
    }

    public void setLength(double length){
        this.length = length;
    }
    public double getLength() {
        return length;
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public void setCars(ArrayList<Car> cars) {
        this.cars = cars;
    }
}
