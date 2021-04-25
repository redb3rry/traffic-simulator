package city;

public class Junction {
    private final int id;
    private final float lightChangeTime;
    private double x,y;

    public Junction(int id, float lightChangeTime, double x, double y) {
        this.id = id;
        this.lightChangeTime = lightChangeTime;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Junction{" +
                "id=" + id +
                ", lightChangeTime=" + lightChangeTime +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public int getId() {
        return id;
    }

    public float getLightChangeTime() {
        return lightChangeTime;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
