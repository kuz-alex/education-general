import java.util.Arrays;

public class FastCollinearPoints {
    private int numberOfSegments = 0;
    private LineSegment[] segmentLines;
    private Segment[] segments = new Segment[4];

    private class Segment {
        public Point[] points;
        public double slope;
    }

    public FastCollinearPoints(Point[] points) {
        validate(points);

        int N = points.length;
        int minCollinearPoints = 3;
        Point[] pointsCopy = Arrays.copyOf(points, points.length);
        Point[] collinearPoints;

        for (int i = 0; i < N; ++i) {
            Point p = points[i];
            Arrays.sort(pointsCopy, p.slopeOrder());

            for (int j = 0; j < N - (minCollinearPoints - 1); ++j) {
                double currentSlope = p.slopeTo(pointsCopy[j]);

                int adjDups = 1;
                for (int k = j + 1; k < pointsCopy.length && currentSlope == p.slopeTo(pointsCopy[k]); k++) {
                    adjDups++;
                }

                if (adjDups >= 3) {
                    Segment seg = collectSegment(p, pointsCopy, currentSlope, j, j + adjDups);

                    if (!checkSegmentAndReplace(seg)) {
                        if (numberOfSegments == segments.length) resize();

                        segments[numberOfSegments++] = seg;
                    }
                }
            }
        }

        segmentLines = new LineSegment[numberOfSegments];
        for (int i = 0; i < numberOfSegments; i++) {
            LineSegment line = new LineSegment(segments[i].points[0],
                    segments[i].points[segments[i].points.length - 1]);
            segmentLines[i] = line;
        }
    }

    public int numberOfSegments() {
        return numberOfSegments;
    }

    public LineSegment[] segments() {
        return Arrays.copyOf(segmentLines, numberOfSegments);
    }

    static private void validate(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException();

        for (int i = 0; i < points.length; ++i) {
            if (points[i] == null || containsPoint(points, i)) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void resize() {
        int newLen = segments.length * 2;
        Segment[] copy = new Segment[newLen];

        for (int i = 0; i < numberOfSegments; ++i)
            copy[i] = segments[i];

        segments = copy;
    }

    private Segment collectSegment(Point origin, Point[] a, double slope, int start, int end) {
        Segment seg = new Segment();
        seg.slope = slope;

        // Create the list of all the points
        Point[] collinearPoints = new Point[1 + end - start];
        collinearPoints[0] = origin;
        for (int i = start, j = 1; i < end; ++i) {
            collinearPoints[j++] = a[i];
        }
        Arrays.sort(collinearPoints);

        seg.points = collinearPoints;
        return seg;
    }

    private boolean checkSegmentAndReplace(Segment seg) {
        for (int i = 0; i < numberOfSegments; ++i) {
            Segment current = segments[i];

            if (current.slope == seg.slope && hasCommonPoint(current.points, seg.points)) {
                // The same slope and either start or end means we're dealing
                // with the same segment.
                if (seg.points.length > current.points.length) {
                    // Replace it in case if it's a longer segment.
                    segments[i] = seg;
                }
                return true;
            }
        }

        return false;
    }

    static private boolean hasCommonPoint(Point[] a, Point[] b) {
        for (Point pointA : a) {
            for (Point pointB : b) {
                if (pointA.compareTo(pointB) == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    static private boolean containsPoint(Point[] points, int index) {
        for (int i = 0; i < index; ++i) {
            if (points[i].compareTo(points[index]) == 0) {
                return true;
            }
        }

        return false;
    }
}

