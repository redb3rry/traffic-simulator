package router;

import city.City;
import city.Junction;
import city.Road;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

//Generate paths for all pairs at init using Floyd-Warshall Algorithm
public class StaticRouter implements IRouter {

    double[][] cityGraph;
    Queue<Road>[][] routes;
    int N;

    private void print(double graph[][], int n){
        for(int i=0; i<n; ++i){
            for(int j=0; j<n; ++j){
                if(graph[i][j]==Double.MAX_VALUE){
                    System.out.print("INF ");
                } else {
                    System.out.print(graph[i][j]+"  ");
                }
            }
            System.out.println();
        }
    }

    private void floydWarshall(City city) {
        double[][] dist = new double[N][N];
        routes = new LinkedList[N][N];
        for (int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                dist[i][j] = cityGraph[i][j];
                routes[i][j] = new LinkedList<>();
                routes[i][j].add(city.getRoadFromTo(city.getJunctions().get(i), city.getJunctions().get(j)));
            }
        }

        for(int k = 0; k < N; k++){
            for (int i = 0; i < N; i++){
                for(int j = 0; j < N; j++){
                    if(dist[i][k] + dist[k][j] < dist[i][j]){
                        dist[i][j] = dist[i][k] + dist[k][j];
                        Queue<Road> newRoute = new LinkedList<>();
                        newRoute.addAll(routes[i][k]);
                        newRoute.addAll(routes[k][j]);
                        routes[i][j] = newRoute;
                    }
                }
            }
        }
//        TESTING
//        print(dist, N);
//        System.out.println((routes[0][6]));
    }

    private double[][] cityToGraph(City city) {
        N = city.getJunctions().size();
        double[][] graph = new double [N][N];
        for(int i = 0; i < N; i ++){
            for(int j = 0; j < N; j ++){
                graph[i][j] = Double.MAX_VALUE;
            }
        }
        for (Road road : city.getRoads()) {
            double len = Math.sqrt(Math.pow(road.getTo().getX()-road.getFrom().getX(),2) + Math.pow(road.getTo().getY()-road.getFrom().getY(),2))/road.getSpeedLimit() + road.getTo().getLightChangeTime();
            graph[road.getFrom().getId()][road.getTo().getId()] = len;
        }
        return graph;
    }

    public StaticRouter(City city) {
        cityGraph = cityToGraph(city);
        floydWarshall(city);
    }

    @Override
    public Queue<Road> findRoute(Junction start, Junction end) {
        return routes[start.getId()][end.getId()];
    }
}
