import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import java.lang.IllegalArgumentException;
import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {
    private TreeSet<Point2D> points;

    public PointSET() {
        points = new TreeSet<Point2D>();
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        // add the point to the set (if it is not already in the set).
        if (contains(p)) return;
        points.add(p);
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        // does the set contain point p? 
        return points.contains(p);
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public int size() {
        // number of points in the set 
        return points.size();
    }

    public void draw() {
        // draw all points to standard draw 
        for (Point2D p : points) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        // all points that are inside the rectangle (or on the boundary) 
        TreeSet<Point2D> pointsInRange = new TreeSet<>();

        for (Point2D p : points) {
            if (rect.contains(p))
                pointsInRange.add(p);
        }
        return pointsInRange;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return null;

        Point2D minP = null;
        for (Point2D curr : points) {
            if (minP == null || p.distanceSquaredTo(curr) < p.distanceSquaredTo(minP)) {
                minP = curr;
            }
        }
        return minP;
    }

    public static void main(String[] args) {
        PointSET ps = new PointSET();
        StdDraw.setPenRadius(0.01);

        int n = Integer.parseInt(args[0]);
        for (int i = 0; i < n; ++i) {
            double x = StdRandom.uniform(0.0, 1.0);
            double y = StdRandom.uniform(0.0, 1.0);
            ps.insert(new Point2D(x, y));
            StdOut.printf("%8.6f %8.6f\n", x, y);
        }

        RectHV rect = new RectHV(StdRandom.uniform(0.0, 1.0), StdRandom.uniform(0.0, 1.0), 1.0, 1.0);
        rect.draw();
        ps.draw();

        int size = 0;
        Point2D lastPointInRange = null;

        for (Point2D p : ps.range(rect)) {
            ++size;
            lastPointInRange = p;
        }

        StdOut.printf("Number of points in the rect: %d.\n", size);

        if (lastPointInRange != null) {
            StdDraw.setPenColor(StdDraw.BLUE);
            lastPointInRange.draw();
            StdDraw.setPenColor(StdDraw.RED);
            ps.nearest(lastPointInRange).draw();
        }
    }
}
