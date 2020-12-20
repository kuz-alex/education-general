import edu.princeton.cs.algs4.*;
import java.lang.Comparable;

public class Selection
{
    public static void sort(Comparable[] a)
    {
        for (int i = 0; i < a.length; ++i) {
            int min = i;
            for (int j = i + 1; j < a.length; ++j)
                if (less(a[j], a[min]))
                    min = j;
            exch(a, i, min);
            //show(a, min);
        }
    }

    private static boolean less(Comparable v, Comparable w)
    {
        return v.compareTo(w) < 0;
    }

    private static void exch(Comparable[] a, int i, int j)
    {
        Comparable temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    private static void show(Comparable[] a, int indexHighlight)
    {
        int N = a.length;
        StdDraw.setXscale(0, N);
        StdDraw.setYscale(0, 100 * 1.5);
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.GRAY);

        for (int i = 0; i < N; ++i) {
            double x = 0 + i;
            double y = (double) a[i] / 2.0;
            double halfWidth = 1;
            double halfHeight = (double) a[i] / 2.0;

            // Clear up a single rectangle.
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledRectangle(x, y, halfWidth, 100);

            // Paint an updated rectangle.
            if (i == indexHighlight)
                StdDraw.setPenColor(StdDraw.RED);
            else
                StdDraw.setPenColor(StdDraw.GRAY);

            StdDraw.filledRectangle(x, y, halfWidth, halfHeight);
        }
        StdDraw.show(100);
    }

    private static boolean isSorted(Comparable[] a)
    {
        for (int i = 1; i < a.length; ++i)
            if (less(a[i], a[i - 1])) return false;
        return true;
    }

    public static void main(String[] args)
    {
        double[] a = StdIn.readAllDoubles();

        Double[] b = new Double[a.length];
        for (int i = 0; i < a.length; ++i) {
            b[i] = new Double(a[i]);
        }
        sort(b);
        assert isSorted(b);
    }
}

