package router;

import city.City;
import city.Junction;
import city.Road;

import java.util.Queue;

public interface IRouter {

    public Queue<Road> findRoute(Junction start, Junction end);
}
