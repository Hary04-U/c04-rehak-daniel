package view;

import model.object.Line;
import model.object.Point;
import model.object.Polygon;
import model.rasterizer.LineRasterizer;
import model.rasterizer.PolygonRasterizer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import static java.awt.Color.*;

public class Pane extends JPanel {

    private final BufferedImage raster;
    private final BufferedImage previewRaster;
    private final LineRasterizer lineRasterizer;
    private final PolygonRasterizer polygonRasterizer;
    private final ArrayList<Point> points = new ArrayList<>();
    private final ArrayList<Polygon> polygons = new ArrayList<>();
    private final ArrayList<Line> lines = new ArrayList<>();
    private Point startPoint;
    private Point endPoint = null;
    private Point helpPoint = null;
    private Point editPoint = null;
    private Line line;
    private String rezim = "";
    private boolean drag = false;
    private boolean polygon = false;
    private boolean edit = false;
    private boolean interpolate = false;
    private boolean gradient = false;
    private int pointIndex;
    private Color barva = CYAN;
    private Color barvaG = null;
    private final Color vychoziBarva = barva;
    private final int pixelRadius = 3;

    public Pane(int width, int height) {
        setPreferredSize(new Dimension(width, height));
        raster = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        previewRaster = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        lineRasterizer = new LineRasterizer(raster);
        polygonRasterizer = new PolygonRasterizer(lineRasterizer);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!polygons.isEmpty()) {
            for (Polygon poly : polygons) {
                polygonRasterizer.vykresliPolygon(raster, poly);
            }
        }

        if (!lines.isEmpty()) {
            for (Line line : lines) {
                lineRasterizer.vykresliLineDDA(line, raster);
            }
        }

        g.drawImage(raster, 0, 0, null);

        Graphics2D gPreview = previewRaster.createGraphics();
        gPreview.setComposite(AlphaComposite.Clear);
        gPreview.fillRect(0, 0, previewRaster.getWidth(), previewRaster.getHeight());
        gPreview.dispose();

        g.setColor(WHITE);
        g.drawString(rezim, 10, 20);
        if (polygon && !interpolate && !gradient) {
            g.drawString("Polygon -> funkce zapnuta", 10, 20);
        } else if (polygon && (interpolate || gradient)) {
            g.drawString("Polygon -> funkce zapnuta", 10, 40);
        }

        if (drag) {
            if (polygon) {
                if (edit) {
                    if (points.size() == 1) {
                        line = new Line(editPoint, editPoint, RED, barvaG);
                        lineRasterizer.vykresliLineDDA(line, previewRaster);
                    } else if (points.size() == 2) {
                        if (pointIndex == 0) {
                            line = new Line(editPoint, points.getLast(), RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                        } else if (pointIndex == 1) {
                            line = new Line(editPoint, points.getFirst(), RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                        }
                    } else if (points.size() > 2) {
                        if (pointIndex == points.size() - 1) {
                            line = new Line(editPoint, points.get(pointIndex - 1), RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                            line = new Line(editPoint, points.getFirst(), RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                        } else if (pointIndex == 0) {
                            line = new Line(editPoint, points.get(pointIndex + 1), RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                            line = new Line(editPoint, points.getLast(), RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                        } else {
                            line = new Line(editPoint, points.get(pointIndex - 1), RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                            line = new Line(editPoint, points.get(pointIndex + 1), RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                        }
                    }
                } else {
                    if (endPoint != null) {
                        if (points.size() > 1) {
                            line = new Line(points.getLast(), endPoint, RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                            line = new Line(points.getFirst(), endPoint, RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                        } else if (points.size() == 1) {
                            line = new Line(points.getFirst(), endPoint, RED, barvaG);
                            lineRasterizer.vykresliLineDDA(line, previewRaster);
                        } else {
                            if (helpPoint == null) {
                                line = new Line(startPoint, endPoint, RED, barvaG);
                                lineRasterizer.vykresliLineDDA(line, previewRaster);
                            } else {
                                line = new Line(helpPoint, endPoint, RED, barvaG);
                                lineRasterizer.vykresliLineDDA(line, previewRaster);
                            }
                        }
                    }
                }
            } else {
                if (helpPoint == null) {
                    line = new Line(startPoint, endPoint, RED, barvaG);
                    lineRasterizer.vykresliLineDDA(line, previewRaster);
                } else {
                    line = new Line(helpPoint, endPoint, RED, barvaG);
                    lineRasterizer.vykresliLineDDA(line, previewRaster);
                }
            }
            g.drawImage(previewRaster, 0, 0, null);
        }

        if (polygon) {
            Color barvaPolygonG = null;
            Color barvaPolygon = YELLOW;
            if (points.size() == 1) {
                line = new Line(points.getFirst(), points.getFirst(), barvaPolygon, barvaPolygonG);
                lineRasterizer.vykresliLineDDA(line, previewRaster);
            }
            if (points.size() > 1) {
                for (int i = 1; i < points.size(); i++) {
                    line = new Line(points.get(i-1), points.get(i), barvaPolygon, barvaPolygonG);
                    lineRasterizer.vykresliLineDDA(line, previewRaster);
                }
            }
            if (points.size() > 2) {
                if (drag && !edit) {
                    barvaPolygon = GRAY;
                }
                line = new Line(points.getFirst(), points.getLast(), barvaPolygon, barvaPolygonG);
                lineRasterizer.vykresliLineDDA(line, previewRaster);
            }

            if (edit) {
                if (drag) {
                    barvaPolygon = GRAY;
                } else {
                    barvaPolygon = YELLOW;
                }
                if (points.size() > 2) {
                    if (pointIndex == points.size() - 1) {
                        line = new Line(points.getLast(), points.get(pointIndex - 1), barvaPolygon, barvaPolygonG);
                        lineRasterizer.vykresliLineDDA(line, previewRaster);
                        line = new Line(points.getLast(), points.getFirst(), barvaPolygon, barvaPolygonG);
                        lineRasterizer.vykresliLineDDA(line, previewRaster);
                    } else if (pointIndex == 0) {
                        line = new Line(points.getFirst(), points.get(pointIndex + 1), barvaPolygon, barvaPolygonG);
                        lineRasterizer.vykresliLineDDA(line, previewRaster);
                        line = new Line(points.getFirst(), points.getLast(), barvaPolygon, barvaPolygonG);
                        lineRasterizer.vykresliLineDDA(line, previewRaster);
                    } else {
                        line = new Line(points.get(pointIndex), points.get(pointIndex - 1), barvaPolygon, barvaPolygonG);
                        lineRasterizer.vykresliLineDDA(line, previewRaster);
                        line = new Line(points.get(pointIndex), points.get(pointIndex + 1), barvaPolygon, barvaPolygonG);
                        lineRasterizer.vykresliLineDDA(line, previewRaster);
                    }
                }
            }
            g.drawImage(previewRaster, 0, 0, null);
        }
    }

    public BufferedImage getRaster() { return raster; }
    public Point getStartPoint() { return startPoint; }
    public Point getEndPoint() { return endPoint; }
    public Point getHelpPoint() { return helpPoint; }
    public Point getEditPoint() { return editPoint; }
    public Line getLine() { return line; }
    public boolean isDrag() { return drag; }
    public boolean isPolygon() { return polygon; }
    public boolean isEdit() { return edit; }
    public boolean isInterpolate() { return interpolate; }
    public boolean isGradient() { return gradient; }
    public int getPointIndex() { return pointIndex; }
    public Color getBarva() { return barva; }
    public Color getBarvaG() { return barvaG; }
    public Color getVychoziBarva() { return vychoziBarva; }
    public int getPixelRadius() { return pixelRadius; }
    public ArrayList<Point> getPoints() { return points; }
    public ArrayList<Polygon> getPolygons() { return polygons; }
    public ArrayList<Line> getLines() { return lines; }


    public void setStartPoint(Point startPoint) { this.startPoint = startPoint; }
    public void setEndPoint(Point endPoint) {this.endPoint = endPoint; }
    public void setHelpPoint(Point helpPoint) { this.helpPoint = helpPoint; }
    public void setEditPoint(Point editPoint) { this.editPoint = editPoint; }
    public void setLine(Line line) { this.line = line; }
    public void setDrag(boolean drag) { this.drag = drag; }
    public void setEdit(boolean edit) { this.edit = edit; }
    public void setPointIndex(int pointIndex) { this.pointIndex = pointIndex; }
    public void setBarva(Color barva) { this.barva = barva; }
    public void setBarvaG(Color barvaG) { this.barvaG = barvaG; }
    public void setPolygon(Boolean polygon) { this.polygon = polygon; }
    public void setRezim(String rezim) {
        interpolate = false;
        gradient = false;

        switch (rezim) {
            case "Interpolace":
                interpolate = true;
                this.rezim = "Interpolace -> funkce zapnuta";
                break;
            case "Gradient":
                gradient = true;
                this.rezim = "Gradient -> funkce zapnuta";
                break;
            case "":
                this.rezim = "";
                break;
        }
    }


}
