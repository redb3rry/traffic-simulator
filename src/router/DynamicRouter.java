package router;

import city.City;
import city.Junction;
import city.Road;
import javafx.util.Pair;

import java.util.*;

//Generate path from start to end at call using Dijkstra's Algorithm
public class DynamicRouter implements IRouter{
    ArrayList<HashMap<Integer, Double>> cityNeighbors;
    int N;
    City city;

    private Queue<Road> dijkstra(int start, int end){
        double[] dist = new double[N];
        int[] parent = new int[N];
        Arrays.fill(dist, Double.MAX_VALUE);
        dist[start] = 0;
        parent[start] = -1;

        PriorityQueue<Pair<Double, Integer>> priorityQueue = new PriorityQueue<Pair<Double, Integer>>((Comparator.comparing(Pair::getKey)));
        priorityQueue.add(new Pair(0.0, start));

        while (priorityQueue.size() > 0){
            Pair<Double, Integer> currentPair = priorityQueue.remove();
            double currentDist = currentPair.getKey();
            int currentNode = currentPair.getValue();

            if(currentDist > dist[currentNode]){
                continue;
            }
            for(HashMap.Entry<Integer, Double> neighbour: cityNeighbors.get(currentNode).entrySet()){
                double distance = neighbour.getValue() + currentDist;

                if (distance < dist[neighbour.getKey()]){
                    dist[neighbour.getKey()] = distance;
                    parent[neighbour.getKey()] = currentNode;
                    priorityQueue.add(new Pair(distance, neighbour.getKey()));
                }
            }
        }
        LinkedList<Road> path = new LinkedList<>();
        int pathNode = end;
        while(parent[pathNode] != -1){
            path.addFirst(city.getRoadFromTo(city.getJunctions().get(parent[pathNode]), city.getJunctions().get(pathNode)));
            pathNode = parent[pathNode];
        }
        return path;
    }

    private ArrayList<HashMap<Integer, Double>> cityGetNeighbors(City city) {
        ArrayList<HashMap<Integer, Double>> neighbors = new ArrayList<HashMap<Integer, Double>>();

        for(int i = 0; i < N; i ++){
            neighbors.add(new HashMap<>());
        }

        for(Road road: city.getRoads()){
            double len = (Math.sqrt(Math.pow(road.getTo().getX() - road.getFrom().getX(), 2) + Math.pow(road.getTo().getY() - road.getFrom().getY(), 2)) / road.getSpeedLimit()) + road.getTo().getLightChangeTime() + ((double) road.getCars().size() / (double) road.getLaneNum());
            neighbors.get(road.getFrom().getId()).put(road.getTo().getId(), len);
        }
        return neighbors;
    }

    public DynamicRouter(City city) {
        this.city = city;
        N = city.getJunctions().size();
        cityNeighbors = cityGetNeighbors(city);
    }

    @Override
    public Queue<Road> findRoute(Junction start, Junction end) {
        return dijkstra(start.getId(), end.getId());
    }
}
