import edu.princeton.cs.algs4.*;
import java.util.NoSuchElementException;

class UnorderedArrayMaxPQ<Item extends Comparable<Item>>
{
    private Item a[];
    private int N = 0;

    public UnorderedArrayMaxPQ(int initialSize) {
        a = (Item[]) new Comparable[initialSize];
    }

    public void insert(Item item) {
        if (a.length == N)
            resize(a.length * 2);

        a[N++] = item;
    }

    public Item delMax() {
        if (isEmpty()) throw new NoSuchElementException();

        int maxI = 0;

        for (int i = 0; i < N; ++i) {
            if (a[i].compareTo(a[maxI]) > 0)
                maxI = i;
        }

        // Swap with the last element and overwrite it with null.
        Item removed = a[maxI];
        a[maxI] = a[N - 1];
        a[--N] = null;
        return removed;
    }

    public boolean isEmpty() {
        return N == 0;
    }

    private void resize(int newLen) {
        Item[] copy = (Item[]) new Object[newLen];

        for (int i = 0; i < a.length; ++i) {
            copy[i] = a[i];
        }

        a = copy;
    }

    // private shrink() {} // if (N < (a.length / 4))

    static public void main(String[] args) {
        // insert a bunch of strings
        String[] strings = {"it", "was", "the", "best", "of", "times", "it", "was", "the", "worst"};

        UnorderedArrayMaxPQ<String> pq = new UnorderedArrayMaxPQ<String>(strings.length);
        for (int i = 0; i < strings.length; i++) {
            pq.insert(strings[i]);
        }

        // delete and print each key
        StdOut.println("In reverse order: ");
        while (!pq.isEmpty()) {
            String key = pq.delMax();
            StdOut.printf("%s ", key);
        }
        StdOut.println();

        Integer[] numbers = {1492, 1783, 1776, 1804, 1865, 1945, 1963, 1918, 2001, 1941};
        UnorderedArrayMaxPQ<Integer> npq = new UnorderedArrayMaxPQ<Integer>(numbers.length);
        for (int i = 0; i < numbers.length; i++) {
            npq.insert(numbers[i]);
        }

        StdOut.println("In reverse order: ");
        while (!npq.isEmpty()) {
            StdOut.printf("%d ", npq.delMax());
        }
        StdOut.println();
    }
}
