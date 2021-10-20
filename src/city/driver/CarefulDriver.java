package city.driver;

public class CarefulDriver implements IDriver{

    String weather;
    double stopBuffer = 55;
    double slowBuffer = 40;
    double speedModifier = -10;

    public CarefulDriver(String weather){
        this.weather = weather;
    }

    @Override
    public double getStopBuffer() {
        if (weather.equals("snow")) {
            return stopBuffer * 1.5;
        } else if (weather.equals("rain")) {
            return stopBuffer * 1.2;
        } else {
            return stopBuffer;
        }
    }

    @Override
    public double getSlowBuffer() {
        if (weather.equals("snow")) {
            return slowBuffer * 1.5;
        } else if (weather.equals("rain")) {
            return slowBuffer * 1.2;
        } else {
            return slowBuffer;
        }
    }

    @Override
    public double getSpeedModifier(double speed) {
        if (weather.equals("snow")) {
            return (speed + speedModifier) * 0.15;
        } else if (weather.equals("rain")) {
            return (speed + speedModifier) * 0.60;
        } else {
            return (speed + speedModifier);
        }
    }

    @Override
    public String getDriverName() {
        return "careful";
    }
}
