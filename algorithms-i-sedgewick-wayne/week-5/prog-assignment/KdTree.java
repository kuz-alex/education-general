import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.lang.IllegalArgumentException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public class KdTree {
    private Node root;
    private int n;

    private static class Node {
        private Point2D p; // the point
        private RectHV rect; // the axis-aligned rectangle associated with the point.
        private Node lb; // the left/bottom subtree
        private Node rt; // the right/top subtree
        public Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    private static class PointWithDistance {
        private Point2D p;
        private double distance;
        public PointWithDistance(Point2D p, Point2D queryP) {
            this.p = p;
            this.distance = p.distanceSquaredTo(queryP);
        }
    }

    public KdTree() {
        n = 0;
    }

    public void insert(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        root = insert(root, p, null, 0);
    }

    private Node insert(Node x, Point2D p, Node prevNode, int level) {
        if (x == null) {
            n++;
            return new Node(p, constructRect(p, prevNode, level));
        }

        level++;
        int cmpResult = cmp(p, x.p, level % 2 == 0);

        if (cmpResult < 0) x.lb = insert(x.lb, p, x, level);
        else x.rt = insert(x.rt, p, x, level);

        return x;
    }

    // Parent node is used to determine the rectange's borders.
    private RectHV constructRect(Point2D curr, Node parent, int level) {
        if (parent == null) return new RectHV(0.0, 0.0, 1.0, 1.0);

        boolean isVertical = level % 2 == 0;
        int cmp = cmp(curr, parent.p, isVertical);
        double xmin = parent.rect.xmin();
        double xmax = parent.rect.xmax();
        double ymin = parent.rect.ymin();
        double ymax = parent.rect.ymax();

        if (cmp < 0 && isVertical) {
            ymax = parent.p.y();
        } else if (cmp > 0 && isVertical) {
            ymin = parent.p.y();
        } else if (cmp < 0 && !isVertical) {
            xmax = parent.p.x();
        } else if (cmp > 0 &&  !isVertical) {
            xmin = parent.p.x();
        }

        return new RectHV(xmin, ymin, xmax, ymax);
    }

    public boolean isEmpty() {
        return n == 0;
    }

    public int size() {
        // number of points in the set 
        return n;
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException();

        int level = 0;
        Queue<Node> q = new Queue<>();
        q.enqueue(root);

        while (!q.isEmpty()) {
            Node curr = q.dequeue();
            if (curr == null) break;

            if (p.equals(curr.p)) return true; // found the point.

            int cmpResult = cmp(p, curr.p, level % 2 == 0);
            if (cmpResult < 0) q.enqueue(curr.lb);
            else q.enqueue(curr.rt);

            level++;
        }

        return false;
    }

    private int cmp(Point2D v, Point2D w, boolean isVertical) {
        return isVertical
            ? Point2D.Y_ORDER.compare(v, w)
            : Point2D.X_ORDER.compare(v, w);
    }

    public void draw() {
        drawRecursive(root, null, 0);
    }

    private void drawRecursive(Node x, Node parent, int level) {
        if (x == null) return;

        // Draw the line
        boolean isVertical = level % 2 == 0;
        level++;

        drawLine(x, parent, isVertical);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        x.p.draw();
        //StdDraw.text(x.p.x(), x.p.y(), x.p.toString());
        StdDraw.setPenRadius(0.001);

        // Recurse
        drawRecursive(x.lb, x, level);
        drawRecursive(x.rt, x, level);
    }

    private void drawLine(Node x, Node parent, boolean isVertical) {
        if (isVertical) StdDraw.setPenColor(StdDraw.RED);
        else            StdDraw.setPenColor(StdDraw.BLUE);

        if (parent == null) {
            StdDraw.line(x.p.x(), 0.0, x.p.x(), 1.0);
            return;
        }

        double xmin, xmax, ymin, ymax;
        xmin = xmax = x.p.x();
        ymin = ymax = x.p.y();
        int cmpResult = cmp(x.p, parent.p, isVertical);

        if (cmpResult < 0 && isVertical) {
            ymin = x.rect.ymin();
            ymax = x.rect.ymax();
        } else if (cmpResult > 0 && isVertical) {
            ymin = x.rect.ymin();
            ymax = x.rect.ymax();
        } else if (cmpResult < 0 && !isVertical) {
            xmin = x.rect.xmin();
            xmax = x.rect.xmax();
        } else if (cmpResult > 0 &&  !isVertical) {
            xmin = x.rect.xmin();
            xmax = x.rect.xmax();
        }

        StdDraw.line(xmin, ymin, xmax, ymax);
    }

    private Iterable<Point2D> keys() {
        ArrayList<Point2D> points = new ArrayList<Point2D>();
        keys(root, points);
        return points;
    }

    private void keys(Node x, ArrayList<Point2D> points) {
        if (x == null) return;

        keys(x.lb, points);
        points.add(x.p);
        keys(x.rt, points);
    }

    private List<List<Point2D>> levelOrder() {
        List<List<Point2D>> result = new ArrayList<>();
        LinkedList<Node> q = new LinkedList<>();
        q.add(root);

        while (!q.isEmpty()) {
            int levelSize = q.size();
            List<Point2D> list = new ArrayList<>();

            for (int i = 0; i < levelSize; ++i) {
                Node curr = q.pop();
                list.add(curr.p);
                if (curr.rt != null) q.push(curr.rt);
                if (curr.lb != null) q.push(curr.lb);
            }
            result.add(list);
        }

        return result;
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException();
        // all points that are inside the rectangle (or on the boundary) 
        ArrayList<Point2D> pointsInRange = new ArrayList<>();

        range(root, rect, pointsInRange);

        return pointsInRange;
    }

    private void range(Node x, RectHV rect, ArrayList<Point2D> pointsInRange) {
        if (x == null) return;

        if (x.lb != null && rect.intersects(x.lb.rect))
            range(x.lb, rect, pointsInRange);

        if (rect.contains(x.p))
            pointsInRange.add(x.p);

        if (x.rt != null && rect.intersects(x.rt.rect))
            range(x.rt, rect, pointsInRange);
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException();
        if (isEmpty()) return null;

        PointWithDistance result = new PointWithDistance(root.p, p);
        nearest(root, p, result, 0);
        return result.p;
    }

    // returns closest node
    private void nearest(Node x, Point2D queryP, PointWithDistance closest, int level) {
        if (x == null) return;
        if (closest.distance < x.rect.distanceSquaredTo(queryP)) return;

        double currentDistance = queryP.distanceSquaredTo(x.p);
        if (currentDistance < closest.distance) {
            closest.p = x.p;
            closest.distance = currentDistance;
        }

        // Determine which side to go into first. queryP
        level++;
        int cmpResult = cmp(queryP, x.p, level % 2 == 0);
        if (cmpResult > 0) {
            nearest(x.rt, queryP, closest, level);
            nearest(x.lb, queryP, closest, level);
        } else {
            nearest(x.lb, queryP, closest, level);
            nearest(x.rt, queryP, closest, level);
        }
    }

    private static void testFromFile(String filename) {
        In in = new In(filename);
        KdTree kdtree = new KdTree();
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            kdtree.insert(new Point2D(x, y));
        }
        kdtree.draw();
        StdOut.printf("Size: %d\n", kdtree.size());

        Point2D nP = kdtree.nearest(new Point2D(0.81, 0.3));
        nP.draw();
        return;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            String filename = args[0];
            testFromFile(filename);
            return;
        }

        KdTree kdtree = new KdTree();
        kdtree.insert(new Point2D(0.75, 0.875));
        kdtree.insert(new Point2D(0.5, 0.75));
        kdtree.insert(new Point2D(0.625, 0.5));
        kdtree.insert(new Point2D(0.125, 1.0));
        kdtree.insert(new Point2D(1.0, 0.625));

        StdOut.printf("%s\n", kdtree.contains(new Point2D(0.5, 0.5)));
    }
}
