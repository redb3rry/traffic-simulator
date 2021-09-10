package city;

import java.util.ArrayList;
import java.util.Queue;

public class Car implements Comparable<Car> {
    private double maxSpeed, currentSpeed;
    private double acceleration, deceleration;
    private Driver driver;
    private int lane;
    private double currentPosition;
    private Junction start, end;
    private Queue<Road> route;

    public Car(float maxSpeed, float currentSpeed, float acceleration, float deceleration, Driver driver, Junction start, Junction end) {
        this.maxSpeed = maxSpeed;
        this.currentSpeed = currentSpeed;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
        this.driver = driver;
        this.start = start;
        this.end = end;
    }

    public double getCurrentSpeed() {
        return currentSpeed;
    }

    public void changeSpeed(double change) {
        this.currentSpeed += change;
    }

    public void setCurrentSpeed(double speed) {
        this.currentSpeed = speed;
    }

    public int getLane() {
        return lane;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public double getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(double currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void move(double change) {
        this.currentPosition += change;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getDeceleration() {
        return deceleration;
    }

    public Driver getDriver() {
        return driver;
    }

    public Junction getStart() {
        return start;
    }

    public Junction getEnd() {
        return end;
    }

    public Queue<Road> getRoute() {
        return route;
    }

    public void setRoute(Queue<Road> route) {
        this.route = route;
    }

    @Override
    public int compareTo(Car o) {
        if (this.currentPosition > o.currentPosition) {
            return 1;
        } else {
            return -1;
        }
    }

    public boolean tryChangeLane(Road road, Car car, int carId, double drawLen) {
        ArrayList<Car> cars = road.getCars();
        Car prevCar = null;
        Car prevCar2 = null;
        if (carId > 0) {
            prevCar = cars.get(carId - 1);
        }
        if (carId > 1) {
            prevCar2 = cars.get(carId - 2);
        }
        double nextCarLane = cars.get(carId + 1).getLane();
        int laneNum = road.getLaneNum();
        if (car.getLane() == nextCarLane) {
            if (prevCar == null && prevCar2 == null) {
                if (car.getLane() == laneNum - 1) {
                    car.setLane(car.getLane() - 1);
                } else {
                    car.setLane(car.getLane() + 1);
                }
                return true;
            } else if (prevCar2 == null) {
                int pcLane = prevCar.getLane();
                double posDif = car.getCurrentPosition() - prevCar.getCurrentPosition();
                if ((pcLane != car.getLane() - 1 || posDif > 1.5 * drawLen) && car.getLane() - 1 >= 0) {
                    car.setLane(car.getLane() - 1);
                    return true;
                } else if ((pcLane != car.getLane() + 1 || posDif > 1.5 * drawLen) && car.getLane() + 1 < laneNum) {
                    car.setLane(car.getLane() + 1);
                    return true;
                }
            } else {
                int pcLane = prevCar.getLane();
                int pc2Lane = prevCar2.getLane();
                double posDif = car.getCurrentPosition() - prevCar.getCurrentPosition();
                double posDif2 = car.getCurrentPosition() - prevCar2.getCurrentPosition();
                if (((pcLane != car.getLane() - 1) || posDif > 1.5 * drawLen) && car.getLane() - 1 >= 0) {
                    if (pc2Lane != car.getLane() - 1 || posDif2 > 1.5 * drawLen) {
                        car.setLane(car.getLane() - 1);
                        return true;
                    }
                } else if (((pcLane != car.getLane() + 1) || posDif > 1.5 * drawLen) && car.getLane() + 1 < laneNum) {
                    if (pc2Lane != car.getLane() + 1 || posDif2 > 1.5 * drawLen) {
                        car.setLane(car.getLane() + 1);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
