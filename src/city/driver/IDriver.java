package city.driver;

public interface IDriver {

    String weather = null;
    double getStopBuffer();
    double getSpeedModifier(double speed);
    double getSlowBuffer();
    String getDriverName();
}
