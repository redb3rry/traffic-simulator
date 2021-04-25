package city;

import java.util.ArrayList;

public class City {
    private ArrayList<Junction> junctions;
    private ArrayList<Road> roads;
    double drawLen;

    public double getDrawLen() {
        return drawLen;
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
    }

    public void moveUp(double step){
        for (Junction junction:junctions) {
            junction.setY(junction.getY()-step);
        }
    }

    public void moveDown(double step){
        for (Junction junction:junctions) {
            junction.setY(junction.getY()+step);
        }
    }

    public void moveRight(double step){
        for (Junction junction:junctions) {
            junction.setX(junction.getX()+step);
        }
    }

    public void moveLeft(double step){
        for (Junction junction:junctions) {
            junction.setX(junction.getX()-step);
        }
    }

    public void zoom(double step){
        drawLen+=step;
        for (Road road:roads){
            Junction from = road.getFrom();
            Junction to = road.getTo();
            int side = road.getSide();
            switch (side) {
                case 0 -> {
                    from.setX(from.getX() - step * 5);
                    to.setX(to.getX() + step * 5);
                }
                case 2 -> {
                    from.setX(from.getX() + step * 5);
                    to.setX(to.getX() - step * 5);
                }
                case 1 -> {
                    from.setY(from.getY() - step * 5);
                    to.setY(to.getY() + step * 5);
                }
                case 3 -> {
                    from.setY(from.getY() + step * 5);
                    to.setY(to.getY() - step * 5);
                }
            }
        }
    }
}
