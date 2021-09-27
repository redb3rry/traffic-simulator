package city;

import router.DynamicRouter;
import router.IRouter;
import router.StaticRouter;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;


public class City {
    private ArrayList<Junction> junctions;
    private ArrayList<Road> roads;
    private ArrayList<Car> cars;
    private int junctionsNum;
    private int maxAllowedCars;
    private double simulationTime;
    private String weather;
    private int carefulDriverMod;
    private int standardDriverMod;
    private int aggressiveDriverMod;
    private int currentCars;
    private final double drawLen;
    private final double offset;
    private int timeElapsed;
    private int tick;
    private float timeMultiplier;
    private IRouter router;
    private String routerType;
    private String reportOutput;

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
        return weather;
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
        cars = new ArrayList<>();

        Properties appProps = new Properties();
        try {
            appProps.load(new FileInputStream("src/app.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        maxAllowedCars = Integer.parseInt(appProps.getProperty("carMax"));
        simulationTime = Double.parseDouble(appProps.getProperty("simulationTime"));
        weather = appProps.getProperty("weather");
        carefulDriverMod = Integer.parseInt(appProps.getProperty("carefulDriverModifier"));
        standardDriverMod = Integer.parseInt(appProps.getProperty("standardDriverModifier"));
        aggressiveDriverMod = Integer.parseInt(appProps.getProperty("aggressiveDriverModifier"));
        routerType = appProps.getProperty("router");
        reportOutput = appProps.getProperty("output");

        if (routerType == "Dynamic") {
            router = new DynamicRouter(this);
        } else {
            router = new StaticRouter(this);
        }
    }

    //FOR TESTING PURPOSES
    public void addRandCar() {
        int randStart = getRandomNumber(0, junctionsNum);
        int randEnd = getRandomNumber(0, junctionsNum);
        while (randStart == randEnd) {
            randEnd = getRandomNumber(0, junctionsNum);
        }
        Car car = new Car(100, 0, getRandomNumber(5, 10), getRandomNumber(40, 80), new Driver(), junctions.get(randStart), junctions.get(randEnd));
        car.setLane(0);
        Queue<Road> foundRoute = router.findRoute(junctions.get(randStart), junctions.get(randEnd));
        Queue<Road> carRoute = new LinkedList<>(foundRoute);
        Road firstRoad = carRoute.remove();
        car.setRoute(carRoute);
        firstRoad.getCars().add(car);
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

//    public void zoom(double step){
//        drawLen+=step;
//        offset=drawLen/4;
//        for (Car car:cars ){
//            car.setCurrentPosition(car.getCurrentPosition()+step);
//        }
//        for (Road road:roads){
//            Junction from = road.getFrom();
//            Junction to = road.getTo();
//            int side = road.getSide();
//            switch (side) {
//                case 0 -> {
//                    from.setX(from.getX() - step * 10);
//                    to.setX(to.getX() + step * 10);
//                }
//                case 2 -> {
//                    from.setX(from.getX() + step * 10);
//                    to.setX(to.getX() - step * 10);
//                }
//                case 1 -> {
//                    from.setY(from.getY() - step * 10);
//                    to.setY(to.getY() + step * 10);
//                }
//                case 3 -> {
//                    from.setY(from.getY() + step * 10);
//                    to.setY(to.getY() - step * 10);
//                }
//            }
//        }
//    }

    public void changeL() {
        for (Junction junction : junctions) {
            junction.changeLights();
        }
    }

    public void updateCity() {
//        System.out.println(timeElapsed);
        updateTrafficLights();
        updateCars();
        timeElapsed += tick;
    }

    private void updateCars() {
        for (Road road : roads) {
            Collections.sort(road.getCars());
            ArrayList<Car> carsToMove = new ArrayList<>();
            for (int i = 0; i < road.getCars().size(); i++) {
                Car car = road.getCars().get(i);
                double nextCarPos = Double.MAX_VALUE;
                //Avoid collision
                ArrayList<Car> carsInFront = new ArrayList<>();
                for (int j = i + 1; (j < road.getCars().size()); j++) {
                    carsInFront.add(road.getCars().get(j));
                }
                boolean slowDown = false;
                for (Car nextCar : carsInFront) {
                    nextCarPos = nextCar.getCurrentPosition();
                    int nextCarLane = nextCar.getLane();
                    if (car.getLane() == nextCarLane && car.getCurrentPosition() + 4 * car.getCurrentSpeed() > nextCarPos - car.getDriver().stopBuffer) {
                        slowDown = true;
                        break;
                    }
                }
                if (slowDown) {
                    if (nextCarPos - car.getCurrentPosition() < 30 && road.getLaneNum() != 1) {
                        if (car.tryChangeLane(road, car, i, drawLen)) {
                            continue;
                        }
                    }
                    car.changeSpeed((-1) * car.getDeceleration() * (tick * timeMultiplier));
                    if (car.getCurrentSpeed() < 1) {
                        car.setCurrentSpeed(0);
                    } else {
                        moveCar(road, car);
                    }
                    continue;
                }
                //Close to junction
                if (road.getLength() - car.getCurrentPosition() < 2) {
                    if (road.getTo().checkForGreenLight(road.getSide())) {
                        carsToMove.add(car);
                    }
                    continue;
                } else if (road.getLength() - car.getCurrentPosition() < 35) {
                    //TODO double check this
                    if (!road.getTo().checkForGreenLight(road.getSide())) {
//                        car.setCurrentSpeed(1);
//                        car.move(1);
//                        continue;
                        car.changeSpeed((-2) * car.getDeceleration() * (tick * timeMultiplier));
                        if (car.getCurrentSpeed() < 0) {
                            car.setCurrentSpeed(1);
                        }
                        moveCar(road, car);
                        continue;
                    }
                }
                //Driving
                if (car.getCurrentPosition() + 4 * car.getCurrentSpeed() > road.getLength() - car.getDriver().stopBuffer && !road.getTo().checkForGreenLight(road.getSide())) {
                    car.changeSpeed((-1) * car.getDeceleration() * (tick * timeMultiplier));
                    if (car.getCurrentSpeed() < 0) {
                        car.setCurrentSpeed(1);
                    }
                } else if (car.getCurrentSpeed() < road.getSpeedLimit() && car.getCurrentSpeed() < car.getMaxSpeed()) {
                    car.changeSpeed(car.getAcceleration() * (tick * timeMultiplier));
                }
                moveCar(road, car);
            }
            //Move cars through junctions
            moveCarsThroughJunctions(road, carsToMove);
        }

    }

    private void moveCar(Road road, Car car) {
        road.updateStatistics(car.getCurrentSpeed());
        car.move(car.getCurrentSpeed());
    }


    private void moveCarsThroughJunctions(Road road, ArrayList<Car> carsToMove) {
        for (Car car : carsToMove) {
            try {
                if (car.getRoute().peek().getCars().get(0).getCurrentPosition() < drawLen) {
                    continue;
                }
            } catch (NullPointerException | IndexOutOfBoundsException ex) {
                assert true;
            }
            road.getCars().remove(car);
            if (car.getRoute().isEmpty()) {
                currentCars--;
                continue;
            }
            Road nextRoad = car.getRoute().remove();
            car.setCurrentPosition(0);
            car.setLane(nextRoad.getLaneNum() - 1);
            nextRoad.getCars().add(car);
        }
    }

    private void updateTrafficLights() {
        for (Junction junction : junctions) {
            if ((timeElapsed * timeMultiplier) % junction.getLightChangeTime() == 0) {
                junction.changeLights();
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
