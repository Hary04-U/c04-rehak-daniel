package model.object;

import java.awt.*;

public class Line {
    private Point startPoint;
    private Point endPoint;
    private Color barva;
    private Color barvaG;

    public Line(Point startPoint, Point endPoint, Color barva, Color barvaG) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.barva = barva;
        this.barvaG = barvaG;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public Color getBarva() {
        return barva;
    }

    public Color getBarvaG() {
        return barvaG;
    }

}
