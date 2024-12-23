package Player.Helper;

import javafx.geometry.Point2D;

public class RenderVect {

    private double a, b, c, d;

    public RenderVect(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public Point2D apply(Point2D p) {
        return new Point2D(a * p.getX() + b * p.getY(), c * p.getX() + d * p.getY());
    }
}
