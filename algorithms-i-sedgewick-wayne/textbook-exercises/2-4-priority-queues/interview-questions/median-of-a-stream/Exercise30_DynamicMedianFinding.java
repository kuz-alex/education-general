import edu.princeton.cs.algs4.StdOut;
import java.util.*;


class Exercise30_DynamicMedianFinding {
    PriorityQueue<Integer> small = null;
    PriorityQueue<Integer> large = null;

    public Exercise30_DynamicMedianFinding() {
        this.large = new PriorityQueue<>();
        this.small = new PriorityQueue<>(Comparator.reverseOrder());
    }

    public void insert(int num) {
        large.offer(num);
        small.offer(large.poll());

        if (large.size() < small.size())
            large.offer(small.poll());
    }

    public double findMedian() {
        if (large.size() > small.size()) {
            return large.peek();
        } else {
            return (large.peek() + small.peek()) / 2.0;
        }
    }

    public Integer deleteMedian() {
        System.out.printf("Small: %s, large: %s.\n", small, large);
        Integer removed = large.peek();
        if (large.size() > small.size()) {
            removed = large.poll();
        } else {
            // Should I delete a single one? Maybe the entry from `large` pq?
            removed = small.poll();
        }

        return removed;
    }

    public static void main(String[] args) {
        Exercise30_DynamicMedianFinding sol = new Exercise30_DynamicMedianFinding();

        sol.insert(1);
        sol.insert(2);
        sol.insert(3);
        sol.insert(4);
        sol.insert(5);
        sol.insert(6);
        sol.insert(7);

        StdOut.println("Median: " + sol.findMedian() + " Expected: 4");
        StdOut.println("Delete Median: " + sol.deleteMedian() + " Expected: 4");

        // When we have an even number of values, pick the left one
        StdOut.println("Median: " + sol.findMedian() + " Expected: 3");

        sol.deleteMedian();
        sol.insert(99);
        sol.insert(100);

        StdOut.println("Median: " + sol.findMedian() + " Expected: 6");
    }
}
