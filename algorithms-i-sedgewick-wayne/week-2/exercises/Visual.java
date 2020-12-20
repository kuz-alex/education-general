import edu.princeton.cs.algs4.*;

public class Visual {
    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int N = a.length;

        StdDraw.setXscale(0, N);
        StdDraw.setYscale(0, N);
        StdDraw.setPenRadius(0.01);
        StdDraw.setPenColor(StdDraw.GRAY);

        for (int i = 0; i < N; ++i) {
            double x = 0.5 + i * 1;
            double y = a[i] / 2.0;
            double hw = 0.5;
            double hh = a[i] / 2.0;

            StdDraw.filledRectangle(x, y, hw, hh);
        }
    }
}
