package model.rasterizer;

import model.object.Line;
import model.object.Polygon;

import java.awt.image.BufferedImage;

public class PolygonRasterizer {

    private LineRasterizer lineRasterizer;
    private Line line;

    public PolygonRasterizer(LineRasterizer lineRasterizer) {
        this.lineRasterizer = lineRasterizer;
    }

    public void vykresliPolygon (BufferedImage raster, Polygon polygon) {
        if (polygon.getPoints().size() > 1) {
            for (int i = 1; i < polygon.getPoints().size(); i++) {
                line = new Line(polygon.getPoints().get(i-1), polygon.getPoints().get(i), polygon.getBarva(), polygon.getBarvaG());
                lineRasterizer.vykresliLineDDA(line, raster);
            }
        }
        if (polygon.getPoints().size() > 2) {
            line = new Line(polygon.getPoints().getFirst(), polygon.getPoints().getLast(), polygon.getBarva(), polygon.getBarvaG());
            lineRasterizer.vykresliLineDDA(line, raster);
        }
    }
}
