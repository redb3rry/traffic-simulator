package city.weather;

public class Snow implements IWeather{
    @Override
    public double getStoppingModifier() {
        return 0.75;
    }

    @Override
    public double getViewModifier() {
        return 0.85;
    }

    @Override
    public String getWeatherName() {
        return "snow";
    }
}
