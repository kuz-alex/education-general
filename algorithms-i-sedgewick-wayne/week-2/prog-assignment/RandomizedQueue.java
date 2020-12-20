import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item>
{
    private int N = 0;
    private Item[] a = (Item[]) new Object[2];

    public boolean isEmpty() { return N == 0; }
    public int size() { return N; }
    private int allocatedSize() { return a.length; }

    private void resize(int newSize) {
        Item[] copy = (Item[]) new Object[newSize];
        for (int i = 0; i < N; ++i) {
            copy[i] = a[i];
        }
        a = copy;
    }

    public void enqueue(Item item) {
        if (item == null)
            throw new IllegalArgumentException();

        if (N == allocatedSize())
            resize(2 * allocatedSize());

        a[N] = item;
        N++;
    }

    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        int randomIndex = StdRandom.uniform(N);
        Item removed = a[randomIndex];
        a[randomIndex] = a[N - 1];
        a[N - 1] = null;
        N--;
        if (N != 0 && N <= allocatedSize() / 4) {
            resize(N);
        }
        return removed;
    }

    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        int randomIndex = StdRandom.uniform(N);
        return a[randomIndex];
    }

    public Iterator<Item> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<Item> {
        public int i = N;
        public Item[] shuffledA = (Item[]) new Object[N];

        public ArrayIterator() {
            for (int i = 0; i < N; ++i) {
                shuffledA[i] = a[i];
            }
            StdRandom.shuffle(shuffledA);
        }

        public boolean hasNext() { return i > 0; }
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return shuffledA[--i];
        }
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static void main(String[] args) {
        RandomizedQueue<Integer> q = new RandomizedQueue<>();

        q.enqueue(3);
        q.enqueue(4);
        q.enqueue(5);
        q.enqueue(2);
        q.enqueue(1);

        StdOut.println("We can iterate through the random queue.");
        for (Integer i : q) {
            StdOut.printf("Iteration: %d\n", i);
        }

        int queueSize = q.size();
        for (int i = 0; i < queueSize; ++i) {
            StdOut.printf("Dequeued: %d\n", q.dequeue());
        }

        int n = 5;
        RandomizedQueue<Integer> queue = new RandomizedQueue<Integer>();
        for (int i = 0; i < n; i++)
            queue.enqueue(i);
        for (int a : queue) {
            for (int b : queue)
                StdOut.print(a + "-" + b + " ");
            StdOut.println();
        }

        StdOut.println("Ensure array shrinks and expands correctly.");
        queue = new RandomizedQueue<Integer>();
        n = 7;
        for (int i = 0; i < n; ++i) {
            queue.enqueue(i);
        }
        StdOut.printf(">\tLogical size is: %d == %d\n", queue.size(), n);
        assert(queue.size() == n);

        for (int i = 0; i < 2; ++i)
            queue.dequeue();
        StdOut.printf("Ensure queue continues to work correctly after being emptied, size %d.\n", queue.size());
        assert(queue.size() == 0);

        n = 5;
        for (int i = 0; i < n; i++)
            queue.enqueue(i);
        for (int a : queue) {
            StdOut.printf(">\t");
            for (int b : queue)
                StdOut.printf("%d-%d ", a, b);
            StdOut.println();
        }
    }
}
