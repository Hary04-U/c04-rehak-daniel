package model.object;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private List<Point> points = new ArrayList<>();
    private Color barva;
    private Color barvaG;

    public Polygon(List<Point> points, Color barva, Color barvaG) {
        this.points = points;
        this.barva = barva;
        this.barvaG = barvaG;
    }

    public List<Point> getPoints() {
        return points;
    }

    public Color getBarva() {
        return barva;
    }

    public Color getBarvaG() {
        return barvaG;
    }
}
