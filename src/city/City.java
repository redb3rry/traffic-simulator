package city;

import city.driver.AggressiveDriver;
import city.driver.CarefulDriver;
import city.driver.IDriver;
import city.driver.StandardDriver;
import city.weather.IWeather;
import city.weather.Normal;
import city.weather.Rain;
import city.weather.Snow;
import router.DynamicRouter;
import router.IRouter;
import router.StaticRouter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;


public class City {
    private final ArrayList<Junction> junctions;
    private final ArrayList<Road> roads;
    private ArrayList<Collision> collisions = new ArrayList<>();
    private ArrayList<Collision> unresolvedCollisions = new ArrayList<>();
    private final int junctionsNum;
    private final int maxAllowedCars;
    private final double simulationTime;
    private final IWeather weather;
    private final int carefulDriverMod;
    private final int standardDriverMod;
    private final int aggressiveDriverMod;
    private int currentCars;
    private final double drawLen;
    private final double offset;
    private int timeElapsed;
    private final int tick;
    private final float timeMultiplier;
    private final IRouter router;
    private final String routerType;
    private final String reportOutput;
    private final int viewDistance = 4;
    private final double collisionBuffer = 2.5;

    public double getDrawLen() {
        return drawLen;
    }

    public double getOffset() {
        return offset;
    }

    public int getMaxAllowedCars() {
        return maxAllowedCars;
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public int getCurrentCars() {
        return currentCars;
    }

    public String getTimeElapsed() {
        double time = Math.round(timeElapsed * timeMultiplier * 100.0) / 100.0;
        return Double.toString(time);
    }

    public String getWeather() {
        return weather.getWeatherName();
    }

    public int getCarefulDriverMod() {
        return carefulDriverMod;
    }

    public int getStandardDriverMod() {
        return standardDriverMod;
    }

    public int getAggressiveDriverMod() {
        return aggressiveDriverMod;
    }

    public String getRouterType() {
        return routerType;
    }

    public String getReportOutput() {
        return reportOutput;
    }

    public ArrayList<Junction> getJunctions() {
        return junctions;
    }

    public ArrayList<Road> getRoads() {
        return roads;
    }

    public ArrayList<Collision> getCollisions() {
        return collisions;
    }

    public City(ArrayList<Junction> junctions, ArrayList<Road> roads) {
        this.junctions = junctions;
        this.junctionsNum = junctions.size();
        this.currentCars = 0;
        this.roads = roads;
        this.drawLen = 10;
        this.offset = 2.5;
        this.timeElapsed = 0;
        this.tick = 1;
        this.timeMultiplier = 0.1f;

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream("src/app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        maxAllowedCars = Integer.parseInt(appProps.getProperty("carMax"));
        simulationTime = Double.parseDouble(appProps.getProperty("simulationTime"));

        String weatherName = appProps.getProperty("weather");
        if (weatherName.equals("Normal")) {
            weather = new Normal();
        } else if (weatherName.equals("Rain")) {
            weather = new Rain();
        } else {
            weather = new Snow();
        }
        carefulDriverMod = Integer.parseInt(appProps.getProperty("carefulDriverModifier"));
        standardDriverMod = Integer.parseInt(appProps.getProperty("standardDriverModifier"));
        aggressiveDriverMod = Integer.parseInt(appProps.getProperty("aggressiveDriverModifier"));
        routerType = appProps.getProperty("router");
        reportOutput = appProps.getProperty("output");

        if (routerType.equals("Dynamic")) {
            router = new DynamicRouter(this);
        } else {
            router = new StaticRouter(this);
        }
    }

    public void addRandCar() {
        int randStart = getRandomNumber(0, junctionsNum);
        int randEnd = getRandomNumber(0, junctionsNum);
        while (randStart == randEnd) {
            randEnd = getRandomNumber(0, junctionsNum);
        }

        IDriver driver;
        int modSum = carefulDriverMod + standardDriverMod + aggressiveDriverMod;
        int randDriver = getRandomNumber(1, modSum + 1);
        if (randDriver <= carefulDriverMod) {
            driver = new CarefulDriver(weather.getWeatherName());
        } else if (randDriver <= carefulDriverMod + standardDriverMod) {
            driver = new StandardDriver(weather.getWeatherName());
        } else {
            driver = new AggressiveDriver(weather.getWeatherName());
        }

        Car car = new Car(100, 0, getRandomNumber(20, 30), getRandomNumber(100, 120), driver, junctions.get(randStart), junctions.get(randEnd));
        car.setLane(3);
        Queue<Road> foundRoute = router.findRoute(junctions.get(randStart), junctions.get(randEnd));
        Queue<Road> carRoute = new LinkedList<>(foundRoute);
        Road firstRoad = carRoute.remove();
        car.setCurrentPosition(getRandomNumber(Math.round(2 * (int) drawLen), Math.round((float) firstRoad.getLength() - 2 * (int) drawLen)));
        car.setRoute(carRoute);
        firstRoad.getCars().add(0, car);
        currentCars++;
    }

    public void moveUp(double step) {
        for (Junction junction : junctions) {
            junction.setY(junction.getY() - step);
        }
    }

    public void moveDown(double step) {
        for (Junction junction : junctions) {
            junction.setY(junction.getY() + step);
        }
    }

    public void moveRight(double step) {
        for (Junction junction : junctions) {
            junction.setX(junction.getX() + step);
        }
    }

    public void moveLeft(double step) {
        for (Junction junction : junctions) {
            junction.setX(junction.getX() - step);
        }
    }

    public void changeL() {
        for (Junction junction : junctions) {
            junction.changeLights();
        }
    }

    public void updateCity() {
        updateTrafficLights();
        updateCars();
        if (maxAllowedCars > currentCars) {
            addRandCar();
        }
        timeElapsed += tick;
        resolveCollisions();
    }

    private void updateCars() {
        for (Road road : roads) {
            Collections.sort(road.getCars());
            checkForCollisions(road);
            ArrayList<Car> carsToMove = new ArrayList<>();
            for (int i = 0; i < road.getCars().size(); i++) {
                Car car = road.getCars().get(i);
                if (car.isDisabled()) {
                    continue;
                }
                if (car.getLane() == 3) {
                    car.tryEnterRoad(road, car, i, drawLen);
                    continue;
                }

                //Avoid collision

                //Prepares list of cars ahead
                ArrayList<Car> carsInFront = new ArrayList<>();
                for (int j = i + 1; (j < road.getCars().size()); j++) {
                    carsInFront.add(road.getCars().get(j));
                }

                double nextCarPos = Double.MAX_VALUE;
                //Looks if cars in front are close enough to slow down
                nextCarPos = checkCarsInFrontForSlow(carsInFront, car, car.getCurrentPosition());
                if (nextCarPos != -1) {
                    //Tries to change lanes
                    if (nextCarPos - car.getCurrentPosition() < car.getDriver().getStopBuffer() && road.getLaneNum() != 1) {
                        if (car.tryChangeLane(road, car, i, drawLen)) {
                            continue;
                        }
                    }
                    //Slows down
                    car.changeSpeed((-1) * (car.getDeceleration() * weather.getStoppingModifier()) * (tick * timeMultiplier));
                    if (car.getCurrentSpeed() < 1) {
                        car.setCurrentSpeed(0);
                    } else {
                        moveCar(road, car);
                    }
                    continue;
                }
                //Check if cars after junction are close enough to slow down
                Road tNextRoad = car.getRoute().peek();
                boolean slowDownPastJunction = checkForSlowPastJunction(car, road, tNextRoad);

                //Close to junction
                if (road.getLength() - car.getCurrentPosition() < 2) {
                    if (road.getTo().checkForGreenLight(road.getSide())) {
                        carsToMove.add(car);
                    }
                    continue;
                } else if (road.getLength() - car.getCurrentPosition() < car.getDriver().getStopBuffer() || slowDownPastJunction) {
                    if (!road.getTo().checkForGreenLight(road.getSide())) {
                        car.changeSpeed((-2) * (car.getDeceleration() * weather.getStoppingModifier()) * (tick * timeMultiplier));
                        if (car.getCurrentSpeed() <= 0) {
                            car.setCurrentSpeed(1);
                        }
                        moveCar(road, car);
                        continue;
                    }
                }
                //Driving
                if (car.getCurrentPosition() + (weather.getViewModifier() * viewDistance) * car.getCurrentSpeed() > road.getLength() - car.getDriver().getSlowBuffer() && !road.getTo().checkForGreenLight(road.getSide())) {
                    car.changeSpeed((-1) * (car.getDeceleration() * weather.getStoppingModifier()) * (tick * timeMultiplier));
                    if (car.getCurrentSpeed() < 0) {
                        car.setCurrentSpeed(1);
                    }
                } else if (car.getCurrentSpeed() < (car.getDriver().getSpeedModifier(road.getSpeedLimit())) && car.getCurrentSpeed() < car.getMaxSpeed()) {
                    car.changeSpeed(car.getAcceleration() * (tick * timeMultiplier));
                }
                moveCar(road, car);
            }
            //Move cars through junctions
            moveCarsThroughJunctions(road, carsToMove);
        }

    }

    private boolean checkForSlowPastJunction(Car car, Road currentRoad, Road nextRoad) {
        double roadLeft = currentRoad.getLength() - car.getCurrentPosition();
        double nextCarDif = 0;
        try {
            nextCarDif = nextRoad.getCars().get(0).getCurrentPosition() + roadLeft;
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return false;
        }
        return (nextCarDif - (weather.getViewModifier() * viewDistance) * car.getCurrentSpeed() < car.getDriver().getSlowBuffer());
    }

    private double checkCarsInFrontForSlow(ArrayList<Car> carsInFront, Car car, double carPosition) {
        for (Car nextCar : carsInFront) {
            double nextCarPos = nextCar.getCurrentPosition();
            int nextCarLane = nextCar.getLane();
            if (car.getLane() == nextCarLane && car.getCurrentPosition() + (weather.getViewModifier() * viewDistance) * car.getCurrentSpeed() > nextCarPos - car.getDriver().getSlowBuffer()) {
                return nextCarPos;
            }
        }
        return -1;
    }

    private void moveCar(Road road, Car car) {
        road.updateStatistics(car.getCurrentSpeed());
        car.move(car.getCurrentSpeed());
    }

    private int getFreeLane(Road road) {
        boolean[] lanes = {false, false, false};
        for (int i = road.getLaneNum() - 1; i >= 0; i--) {
            lanes[i] = true;
        }
        for (Car car : road.getCars()) {
            if (car.getLane() == 3) {
                continue;
            }
            if (car.getCurrentPosition() < 3 * drawLen) {
                lanes[car.getLane()] = false;
            }
        }
        for (int i = road.getLaneNum() - 1; i >= 0; i--) {
            if (lanes[i]) {
                return i;
            }
        }
        return -1;
    }

    private void moveCarsThroughJunctions(Road road, ArrayList<Car> carsToMove) {
        for (Car car : carsToMove) {
            if (car.getRoute().isEmpty()) {
                road.getCars().remove(car);
                currentCars--;
                continue;
            }
            Road tNextRoad = car.getRoute().peek();
            ArrayList<Car> carsInFront = tNextRoad.getCars();
            if (!carsInFront.isEmpty()) {
                int l = getFreeLane(tNextRoad);
                if (l == -1) {
                    continue;
                }
                car.setLane(l);
            }
            road.getCars().remove(car);
            Road nextRoad = car.getRoute().remove();
            if (road.getSide() != nextRoad.getSide()) {
                car.changeSpeed((-2) * (car.getDeceleration() * weather.getStoppingModifier()) * (tick * timeMultiplier));
            }
            car.setCurrentPosition(0);
            nextRoad.getCars().add(0, car);
        }
    }

    private void checkForCollisions(Road road) {
        ArrayList<Car> cars = road.getCars();
        for (int i = 0; i < cars.size() - 1; i++) {
            if (cars.get(i).getCurrentPosition() == 0 || cars.get(i).isDisabled() || cars.get(i).getLane() == 3) {
                continue;
            }
            try {
                if (checkCollision(cars.get(i), cars.get(i + 1))) {
                    addCollision(cars.get(i), cars.get(i + 1), road);
                } else if (checkCollision(cars.get(i), cars.get(i + 2))) {
                    addCollision(cars.get(i), cars.get(i + 2), road);
                } else if (checkCollision(cars.get(i), cars.get(i + 3))) {
                    addCollision(cars.get(i), cars.get(i + 3), road);
                }
            } catch (NullPointerException | IndexOutOfBoundsException e) {
                continue;
            }
        }
    }

    private boolean checkCollision(Car car1, Car car2) {
        if (car1.getCurrentPosition() - drawLen > car2.getCurrentPosition() || (car1.getCurrentSpeed() == 0 && car2.getCurrentSpeed() == 0)) {
            return false;
        }
        return car1.getCurrentPosition() > car2.getCurrentPosition() - (drawLen - collisionBuffer) && car1.getLane() == car2.getLane();
    }

    private void addCollision(Car car1, Car car2, Road road) {
        Collision collision = new Collision(car1, car2, car1.getCurrentSpeed(), car2.getCurrentSpeed(), road, car1.getCurrentPosition(), Math.round(timeElapsed * timeMultiplier * 100.0) / 100.0);
        collisions.add(collision);
        unresolvedCollisions.add(collision);
        car1.setDisabled(true);
        car2.setDisabled(true);
    }

    private void resolveCollisions() {
        ArrayList<Collision> collisionsResolved = new ArrayList<>();
        for (Collision collision : unresolvedCollisions) {
            if (timeElapsed * timeMultiplier >= collision.getCollisionTime() + collision.getResponseTime()) {
                collisionsResolved.add(collision);
                collision.getRoad().getCars().remove(collision.getCar1());
                collision.getRoad().getCars().remove(collision.getCar2());
                currentCars-=2;
            }
        }
        unresolvedCollisions.removeAll(collisionsResolved);
    }

    private void updateTrafficLights() {
        for (Junction junction : junctions) {
            double currentTime = timeElapsed * timeMultiplier;
            if (junction.isUpDownLight() && junction.getLastLightChange() + junction.getUpDownLightChangeTime() <= currentTime) {
                junction.changeLights();
                junction.setLastLightChange(currentTime);
            } else if (junction.isLeftRightLight() && junction.getLastLightChange() + junction.getLeftRightLightChangeTime() <= currentTime) {
                junction.changeLights();
                junction.setLastLightChange(currentTime);
            }
        }
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public Road getRoadFromTo(Junction from, Junction to) {
        for (Road road : roads) {
            if (road.getFrom() == from && road.getTo() == to) {
                return road;
            }
        }
        return null;
    }
}
