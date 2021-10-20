package city.driver;

public class AggressiveDriver implements IDriver{

    String weather;
    double stopBuffer = 25;
    double slowBuffer = 12.5;
    double speedModifier = 20;

    public AggressiveDriver(String weather){
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
            return (speed + speedModifier) * 0.45;
        } else if (weather.equals("rain")) {
            return (speed + speedModifier) * 0.9;
        } else {
            return (speed + speedModifier);
        }
    }

    @Override
    public String getDriverName() {
        return "aggressive";
    }
}
