package city;

public class Car {
    private float maxSpeed, currentSpeed;
    private float acceleration, deceleration;
    private Driver driver;
    private int lane;
    private double currentPosition;

    public Car(float maxSpeed, float currentSpeed, float acceleration, float deceleration, Driver driver) {
        this.maxSpeed = maxSpeed;
        this.currentSpeed = currentSpeed;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
        this.driver = driver;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(float currentSpeed) {
        this.currentSpeed = currentSpeed;
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

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public float getDeceleration() {
        return deceleration;
    }

    public Driver getDriver() {
        return driver;
    }
}
