package city;

public class Collision {
    private Car car1, car2;
    private double speed1, speed2;
    private Road road;
    private double roadPosition;
    private double collisionTime;
    private double responseTime;
    private double traffic;

    public Collision(Car car1, Car car2, double speed1, double speed2, Road road, double roadPosition, double collisionTime) {
        this.car1 = car1;
        this.car2 = car2;
        this.speed1 = speed1;
        this.speed2 = speed2;
        this.road = road;
        this.roadPosition = roadPosition;
        this.collisionTime = collisionTime;
        this.responseTime = determineResponseTime();
    }

    public Car getCar1() {
        return car1;
    }

    public Car getCar2() {
        return car2;
    }

    public double getSpeed1() {
        return speed1;
    }

    public double getSpeed2() {
        return speed2;
    }

    public Road getRoad() {
        return road;
    }

    public double getRoadPosition() {
        return roadPosition;
    }

    public double getCollisionTime() {
        return collisionTime;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    private double determineResponseTime(){
        traffic = road.getTraffic(10) * 0.2;
        double severity = speed1 + speed2;
        return traffic + severity;
    }

    @Override
    public String toString() {
        return "Collision{" +
                "car1=" + car1 +
                ", car2=" + car2 +
                ", speed1=" + speed1 +
                ", speed2=" + speed2 +
                ", roadFrom=" + road.getFrom().getId() +
                ", roadTo=" + road.getTo().getId() +
                ", roadPosition=" + roadPosition +
                ", collisionTime=" + collisionTime +
                '}';
    }

    public String prettyPrintCollision() {
        return "Road " + road.getFrom().getId() + " -> " + road.getTo().getId() +
                ", traffic = " + traffic + ", collision speed = " + speed1 + " -> " + speed2 +
                ", road position = " + roadPosition + ", collision time = " + collisionTime +
                ", response time = " + responseTime +
                ", drivers = " +car1.getDriver().getDriverName()+ " -> " +car2.getDriver().getDriverName();
    }
}
