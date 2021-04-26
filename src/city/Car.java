package city;

import java.util.ArrayList;
import java.util.Queue;

public class Car {
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

    public void setCurrentSpeed(double speed){
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

    public void move(double change){
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
}
