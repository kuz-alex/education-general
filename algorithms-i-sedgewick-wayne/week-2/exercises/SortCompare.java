import edu.princeton.cs.algs4.*;
import java.util.*;

public class SortCompare {
    public static double time(String alg, Double[] a) { 
        Stopwatch sw = new Stopwatch(); 
        if      (alg.equals("Insertion"))       Insertion.sort(a); 
        else if (alg.equals("Selection"))       Selection.sort(a); 
        else throw new IllegalArgumentException("Invalid algorithm: " + alg);
        return sw.elapsedTime(); 
    } 

    public static Double[] generateList(int n) {
        Double[] a = new Double[n];
        for (int i = 0; i < n; ++i) {
            a[i] = StdRandom.uniform(0.0, 1.0);
        }
        return a;
    }

    public static void main(String[] args) {
        StdOut.println("Insertion sort\t\tSelection sort");
        for (int n = 250; n <= 64000; n += n) {
            Double[] a = generateList(n);
            Double[] b = generateList(n);

            StdOut.printf("%7d %7.1f\t\t", n, time("Insertion", a));
            StdOut.printf("%7d %7.1f\n", n, time("Selection", b));
        }
    }
}
