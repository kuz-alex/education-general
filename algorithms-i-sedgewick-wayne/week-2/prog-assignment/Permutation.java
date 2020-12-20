import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;

public class Permutation {
    static public void main(String[] args) {
        int n = Integer.parseInt(args[0]);

        RandomizedQueue<String> queue = new RandomizedQueue<>();
        String s = StdIn.readString();
        while (!StdIn.isEmpty()) {
            queue.enqueue(s);
            s = StdIn.readString();
        }

        for (int i = 0; i < n; ++i) {
            StdOut.println(queue.dequeue());
        }
    }
}
