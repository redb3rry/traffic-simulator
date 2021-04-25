package file;

import city.City;
import city.Junction;
import city.Road;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class CityReader {
    private ArrayList<Junction> junctions;
    private ArrayList<Road> roads;
    private File file;
    private Scanner scanner;

    public CityReader(String filePath) {
        this.file = new File(filePath);
        try {
            scanner = new Scanner(file).useLocale(Locale.ENGLISH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        junctions = new ArrayList<>();
        roads = new ArrayList<>();
    }

    public City readCity() {
        while (scanner.hasNext()) {
            int num = scanner.nextInt();
            for (int i = 0; i < num; i++) {
                int id = scanner.nextInt();
                float time = scanner.nextFloat();
                double x = scanner.nextDouble();
                double y = scanner.nextDouble();
                junctions.add(new Junction(id, time, x, y));
            }
            num = scanner.nextInt();
            for (int i = 0; i < num; i++) {
                int from = scanner.nextInt();
                int to = scanner.nextInt();
                int speed = scanner.nextInt();
                int lanes = scanner.nextInt();
                Junction toJ = junctions.get(to);
                Junction fromJ = junctions.get(from);
                roads.add(new Road(toJ, fromJ, speed, lanes));
            }
        }

        return new City(junctions, roads);
    }
}
