import java.util.Arrays;

public class BruteCollinearPoints
{
    private int numberOfSegments = 0;
    private LineSegment[] segmentLines;
    private Segment[] segments = new Segment[4];

    private class Segment {
        public Point origin;
        public Point end;
        public double slope;
        public int pointsLength;

        public Segment(Point origin, Point end, double slope, int pointsLength) {
            this.origin = origin;
            this.end = end;
            this.slope = slope;
            this.pointsLength = pointsLength;
        }
    }

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        validate(points);
        int N = points.length;
        Point[] pointsCopy = Arrays.copyOf(points, N);

        for (int i = 0; i < N - 3; i++) {
            for (int j = i + 1; j < N - 2; j++) {
                double slope1 = pointsCopy[i].slopeTo(pointsCopy[j]);

                for (int k = j + 1; k < N - 1; k++) {
                    double slope2 = pointsCopy[j].slopeTo(pointsCopy[k]);
                    if (slope1 != slope2) continue;

                    for (int w = k + 1; w < N; w++) {
                        double slope3 = pointsCopy[k].slopeTo(pointsCopy[w]);
                        if (slope2 != slope3) continue;

                        // We found a line segment of 4 pointsCopy.
                        Point[] list = {pointsCopy[i], pointsCopy[j], pointsCopy[k], pointsCopy[w]};
                        Arrays.sort(list);

                        Segment seg = new Segment(list[0], list[3], slope1, 4);

                        if (!containsSegment(seg)) {
                            if (numberOfSegments == segments.length) resize();

                            segments[numberOfSegments++] = seg;
                        }
                    }
                }
            }
        }

        segmentLines = new LineSegment[numberOfSegments];
        for (int i = 0; i < numberOfSegments; i++) {
            LineSegment line = new LineSegment(segments[i].origin, segments[i].end);
            segmentLines[i] = line;
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return numberOfSegments;
    }

    // the line segments
    public LineSegment[] segments() {
        return Arrays.copyOf(segmentLines, numberOfSegments);
    }

    private void validate(Point[] points) {
        if (points == null)
            throw new IllegalArgumentException();

        for (int i = 0; i < points.length; ++i) {
            if (points[i] == null || containsPoint(points, i)) {
                throw new IllegalArgumentException();
            }
        }
    }

    private boolean containsPoint(Point[] points, int index) {
        for (int i = 0; i < index; ++i) {
            if (points[i].compareTo(points[index]) == 0) {
                return true;
            }
        }

        return false;
    }

    private boolean containsSegment(Segment seg) {
        for (int i = 0; i < numberOfSegments; ++i) {
            if (segments[i].origin.compareTo(seg.origin) == 0
                    && segments[i].slope == seg.slope) {
                return true;
            }
        }

        return false;
    }

    private void resize() {
        int newLen = segments.length * 2;
        Segment[] copy = new Segment[newLen];

        for (int i = 0; i < numberOfSegments; ++i)
            copy[i] = segments[i];

        segments = copy;
    }
}
