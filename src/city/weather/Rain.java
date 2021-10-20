package city.weather;

public class Rain implements IWeather{
    @Override
    public double getStoppingModifier() {
        return 0.9;
    }

    @Override
    public double getViewModifier() {
        return 0.95;
    }

    @Override
    public String getWeatherName() {
        return "rain";
    }
}
