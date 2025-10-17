package controller;

import model.object.Line;
import model.object.Point;
import model.object.Polygon;
import view.Pane;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import static model.rasterizer.InterpolateColor.interpolateColor;

public class Controller2D implements Controller {
    private Pane panel;

    public Controller2D(Pane panel) {
        this.panel = panel;
        listeners();
    }

    @Override
    public void listeners() {
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (panel.isPolygon()) {
                    Point editpoint = new Point(e.getX(), e.getY());
                    for (int i = 0; i < panel.getPoints().size(); i++) {
                        if (Math.abs(editpoint.getX() - panel.getPoints().get(i).getX()) <= panel.getPixelRadius() && Math.abs(editpoint.getY() - panel.getPoints().get(i).getY()) <= panel.getPixelRadius()) {
                            panel.setEdit(true);
                            panel.setPointIndex(i);
                        }
                    }
                }
                panel.setStartPoint(new Point(e.getX(), e.getY()));
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (panel.isPolygon()) {
                    if (panel.isDrag()) {
                        if (panel.isEdit() && panel.getEditPoint() != null) {
                            if (panel.getEditPoint().getX() < panel.getRaster().getWidth() && panel.getEditPoint().getX() >= 0 &&
                                    panel.getEditPoint().getY() < panel.getRaster().getHeight() && panel.getEditPoint().getY() >= 0) {
                                panel.getPoints().set(panel.getPointIndex(), panel.getEditPoint());
                            }
                            panel.setEdit(false);
                            panel.setEndPoint(null);
                        } else {
                            if (panel.getPoints().isEmpty()) {
                                panel.getPoints().add(panel.getStartPoint());
                                panel.getPoints().add(panel.getEndPoint());
                            } else {
                                if (e.getX() < panel.getRaster().getWidth() && e.getX() >= 0 &&
                                        e.getY() < panel.getRaster().getHeight() &&  e.getY() >= 0) {
                                    panel.getPoints().add(new Point(e.getX(), e.getY()));
                                }
                            }
                        }
                    } else {
                        panel.getPoints().add(new Point(e.getX(), e.getY()));
                    }
                } else {
                    if (panel.isDrag()) {
                        if (panel.getHelpPoint() != null) {
                            if (panel.getEndPoint().getX() < panel.getRaster().getWidth() && panel.getEndPoint().getX() >= 0 &&
                                    panel.getEndPoint().getY() < panel.getRaster().getHeight() && panel.getEndPoint().getY() >= 0) {
                                panel.setLine(new Line(panel.getHelpPoint(), panel.getEndPoint(), panel.getBarva(), panel.getBarvaG()));
                                panel.getLines().add(panel.getLine());
                                panel.setHelpPoint(null);
                            }
                        } else {
                            if (panel.getEndPoint().getX() < panel.getRaster().getWidth() && panel.getEndPoint().getX() >= 0 &&
                                    panel.getEndPoint().getY() < panel.getRaster().getHeight() && panel.getEndPoint().getY() >= 0) {
                                panel.setLine(new Line(panel.getStartPoint(), panel.getEndPoint(), panel.getBarva(), panel.getBarvaG()));
                                panel.getLines().add(panel.getLine());
                            }
                        }
                    } else {
                        panel.setEndPoint(new Point(e.getX(), e.getY()));
                        if (panel.getHelpPoint() != null) {
                            panel.setLine(new Line(panel.getHelpPoint(), panel.getEndPoint(), panel.getBarva(), panel.getBarvaG()));
                            panel.getLines().add(panel.getLine());
                            panel.setHelpPoint(null);
                        } else {
                            panel.setHelpPoint(panel.getStartPoint());
                            panel.getRaster().setRGB(panel.getHelpPoint().getX(), panel.getHelpPoint().getY(), panel.getBarva().getRGB());
                        }
                    }
                }
                panel.setDrag(false);
                panel.repaint();
            }
        });

        panel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (panel.isEdit()) {
                    panel.setEditPoint(new Point(e.getX(), e.getY()));
                } else {
                    if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0 && !panel.isPolygon()) {
                        int dx = e.getX() - panel.getStartPoint().getX();
                        int dy = e.getY() - panel.getStartPoint().getY();

                        double angle = Math.atan2(dy, dx);
                        double step = Math.toRadians(45);
                        double snapped = Math.round(angle / step) * step;

                        double length = Math.sqrt(dx * dx + dy * dy);
                        int newX = panel.getStartPoint().getX() + (int) (length * Math.cos(snapped));
                        int newY = panel.getStartPoint().getY() + (int) (length * Math.sin(snapped));

                        panel.setEndPoint(new Point(newX, newY));
                    } else {
                        panel.setEndPoint(new Point(e.getX(), e.getY()));
                    }
                }
                panel.setDrag(true);
                panel.repaint();
            }
        });

        panel.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    if (!panel.isPolygon()) {
                        panel.setPolygon(true);
                        panel.setHelpPoint(null);
                    } else {
                        panel.setPolygon(false);
                        Polygon aktualPolygon = new Polygon(new ArrayList<>(panel.getPoints()), panel.getBarva(), panel.getBarvaG());
                        panel.getPolygons().add(aktualPolygon);
                        panel.getPoints().clear();
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_I) {
                    if (!panel.isInterpolate()) {
                        panel.setRezim("Interpolace");
                        panel.setBarva(interpolateColor(panel.getVychoziBarva(), Color.RED, 0.4f));
                        panel.setBarvaG(null);
                    } else {
                        panel.setRezim("");
                        panel.setBarva(panel.getVychoziBarva());
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_G) {
                    if (!panel.isGradient()) {
                        panel.setRezim("Gradient");
                        panel.setBarva(panel.getVychoziBarva());
                        panel.setBarvaG(Color.RED);
                    } else {
                        panel.setRezim("");
                        panel.setBarvaG(null);
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_C) {
                    clean();
                }
                panel.repaint();
            }
        });
    }

    public void clean() {
        Graphics g = panel.getRaster().createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, panel.getRaster().getWidth(), panel.getRaster().getHeight());
        g.dispose();
        if (!panel.getPoints().isEmpty()) {
            panel.getPoints().clear();
        }
        if (!panel.getPolygons().isEmpty()) {
            panel.getPolygons().clear();
        }
        if (!panel.getLines().isEmpty()) {
            panel.getLines().clear();
        }
        panel.setHelpPoint(null);
    }
}

