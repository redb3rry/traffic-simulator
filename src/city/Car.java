package city;

import city.driver.IDriver;

import java.util.ArrayList;
import java.util.Queue;

public class Car implements Comparable<Car> {
    private double maxSpeed, currentSpeed;
    private double acceleration, deceleration;
    private IDriver driver;
    private int lane;
    private double currentPosition;
    private Junction start, end;
    private Queue<Road> route;
    private boolean isDisabled;

    public Car(float maxSpeed, float currentSpeed, float acceleration, float deceleration, IDriver driver, Junction start, Junction end) {
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
        if (this.currentSpeed < 0) {
            this.currentSpeed = 0;
        }
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

    public IDriver getDriver() {
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

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    @Override
    public int compareTo(Car o) {
        if (this.currentPosition > o.currentPosition) {
            return 1;
        } else {
            return -1;
        }
    }

    public void tryEnterRoad(Road road, Car car, int carId, double drawLen) {
        ArrayList<Car> cars = road.getCars();
        int laneToEnter = road.getLaneNum() - 1;
        if (road.getCars().size() == 1) {
            car.setLane(laneToEnter);
        }
        int startId = carId - 5;
        int endId = carId + 5;
        if (startId < 0)
            startId = 0;
        if (endId > cars.size())
            endId = cars.size();
        boolean canEnter = true;
        for (int i = startId; i < endId; i++) {
            Car nCar = cars.get(i);
            double posDif = 0;
            if( i < carId) {
                posDif = car.getCurrentPosition() - nCar.getCurrentPosition();
            } else {
                posDif = nCar.getCurrentPosition() - car.getCurrentPosition();
            }
            if (nCar.getLane() != laneToEnter) {
                continue;
            } else if (posDif < 3 * drawLen){
                canEnter = false;
            }
        }
        if(canEnter)
            car.setLane(laneToEnter);
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
