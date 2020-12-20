import java.lang.Math; 
import edu.princeton.cs.algs4.StdStats;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

public class PercolationStats {
    private double[] results;
    private int T;
    private int n;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        results = new double[trials];

        for (int i = 0; i < trials; ++i) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int randomRow = StdRandom.uniform(1, n + 1);
                int randomCol = StdRandom.uniform(1, n + 1);
                if (!perc.isOpen(randomRow, randomCol)) {
                    perc.open(randomRow, randomCol);
                }
            }

            results[i] = (double) perc.numberOfOpenSites() / (n * n);
        }

        this.n = n;
        this.T = trials;
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(results);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(results);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - ((1.96 * stddev()) / Math.sqrt(T));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + ((1.96 * stddev()) / Math.sqrt(T));
    }

    // test client (see below)
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int T = Integer.parseInt(args[1]);

        Stopwatch timer = new Stopwatch();
        PercolationStats percStats = new PercolationStats(n, T);
        System.out.println("Time elapsed: " + timer.elapsedTime());
        System.out.println("mean                    = " + percStats.mean());
        System.out.println("stddev                  = " + percStats.stddev());
        System.out.println("95% confidence interval = [" + percStats.confidenceLo() + " " + percStats.confidenceHi() + "]");
    }
}
