package city;

public class Junction {
    private final int id;
    private final float leftRightLightChangeTime;
    private final float upDownLightChangeTime;
    private double lastLightChange = 0;
    private double x,y;
    private boolean upDownLight;
    private boolean leftRightLight;

    public Junction(int id, float leftRightLightChangeTime, float upDownLightChangeTime, double x, double y) {
        this.id = id;
        this.leftRightLightChangeTime = leftRightLightChangeTime;
        this.upDownLightChangeTime = upDownLightChangeTime;
        this.x = x;
        this.y = y;
        upDownLight = true;
        leftRightLight = false;
    }

    @Override
    public String toString() {
        return "Junction{" +
                "id=" + id +
                ", leftRightLightChangeTime=" + leftRightLightChangeTime +
                ", upDownLightChangeTime=" + upDownLightChangeTime +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public int getId() {
        return id;
    }

    public float getLeftRightLightChangeTime() {
        return leftRightLightChangeTime;
    }

    public float getUpDownLightChangeTime() {
        return upDownLightChangeTime;
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

    public boolean checkForGreenLight(int side){
        if(side == 0 || side == 2){
            return leftRightLight;
        } else {
            return upDownLight;
        }
    }

    public void changeLights(){
        leftRightLight = !leftRightLight;
        upDownLight = !upDownLight;
    }

    public double getLastLightChange() {
        return lastLightChange;
    }

    public void setLastLightChange(double lastLightChange) {
        this.lastLightChange = lastLightChange;
    }

    public boolean isUpDownLight() {
        return upDownLight;
    }

    public boolean isLeftRightLight() {
        return leftRightLight;
    }
}
