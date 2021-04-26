package city;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class City {
    private ArrayList<Junction> junctions;
    private ArrayList<Road> roads;
    private ArrayList<Car> cars;
    private double drawLen;
    private double offset;
    private float timeElapsed;
    private float tick;
    private double acceptableTimeError=0.001;

    public double getDrawLen() {
        return drawLen;
    }

    public double getOffset() {
        return offset;
    }

    public ArrayList<Junction> getJunctions() {
        return junctions;
    }

    public ArrayList<Road> getRoads() {
        return roads;
    }

    public City(ArrayList<Junction> junctions, ArrayList<Road> roads) {
        this.junctions = junctions;
        this.roads = roads;
        this.drawLen = 10;
        this.offset = 2.5;
        this.timeElapsed = 0;
        this.tick = 0.1f;
        cars = new ArrayList<>();

        //TESTING
        ArrayList<Car> cars2 = new ArrayList<>();
        Car testCar = new Car(100, 0, 5, 10, new Driver(), junctions.get(0), junctions.get(3));
        Queue<Road> route = new LinkedList<>();
        route.add(roads.get(2));
        route.add(roads.get(4));
        route.add(roads.get(6));
        route.add(roads.get(0));
        route.add(roads.get(2));
        route.add(roads.get(4));
        route.add(roads.get(6));
        testCar.setRoute(route);
        testCar.setLane(1);
        cars.add(testCar);
        roads.get(0).setCars(cars);
        Car testCar2 = new Car(100, 0, 5, 10, new Driver(), junctions.get(0), junctions.get(3));
        Queue<Road> route2 = new LinkedList<>();
        route2.add(roads.get(5));
        route2.add(roads.get(3));
        route2.add(roads.get(1));
        route2.add(roads.get(7));
        route2.add(roads.get(5));
        route2.add(roads.get(3));
        route2.add(roads.get(1));
        testCar2.setRoute(route2);
        testCar.setLane(0);
        cars2.add(testCar2);
        roads.get(7).setCars(cars2);
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
        System.out.println(timeElapsed);
        for (Junction junction : junctions) {
            if (timeElapsed % junction.getLightChangeTime() < acceptableTimeError || junction.getLightChangeTime() - timeElapsed % junction.getLightChangeTime() < acceptableTimeError) {
                junction.changeLights();
            }
        }
        for (Road road : roads) {
            ArrayList<Car> carsToMove = new ArrayList<>();
            for (Car car : road.getCars()) {
                //Close to junction
                if (road.getLength() - car.getCurrentPosition() < 2) {
                    if (road.getTo().checkForGreenLight(road.getSide())) {
                        carsToMove.add(car);
                        continue;
                    } else {
                        continue;
                    }
                } else if (road.getLength() - car.getCurrentPosition() < 35) {
                    car.setCurrentSpeed(1);
                    car.move(1);
                    continue;
                }

                //Driving
                if (car.getCurrentPosition() + 4 * car.getCurrentSpeed() > road.getLength() - car.getDriver().stopBuffer) {
                    car.changeSpeed((-1) * car.getDeceleration() * tick);
                } else if (car.getCurrentSpeed() < road.getSpeedLimit() && car.getCurrentSpeed() < car.getMaxSpeed()) {
                    car.changeSpeed(car.getAcceleration() * tick);
                }
                car.move(car.getCurrentSpeed());
            }
            //Move cars through junctions
            for (Car car : carsToMove) {
                road.getCars().remove(car);
                if (car.getRoute().isEmpty()) {
                    continue;
                }
                Road nextRoad = car.getRoute().remove();
                car.setCurrentPosition(0);
                nextRoad.getCars().add(car);
            }
        }
        timeElapsed += tick;
    }
}
