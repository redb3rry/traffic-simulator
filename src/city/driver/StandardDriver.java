package city.driver;

public class StandardDriver implements IDriver {

    String weather;
    double stopBuffer = 35;
    double slowBuffer = 20;
    double speedModifier = 0;

    public StandardDriver(String weather) {
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
            return (speed + speedModifier) * 0.25;
        } else if (weather.equals("rain")) {
            return (speed + speedModifier) * 0.75;
        } else {
            return (speed + speedModifier);
        }
    }

    @Override
    public String getDriverName() {
        return "standard";
    }
}
