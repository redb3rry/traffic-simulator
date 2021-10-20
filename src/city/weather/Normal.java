package city.weather;

public class Normal implements IWeather{
    @Override
    public double getStoppingModifier() {
        return 1;
    }

    @Override
    public double getViewModifier() {
        return 1;
    }

    @Override
    public String getWeatherName() {
        return "normal";
    }
}
