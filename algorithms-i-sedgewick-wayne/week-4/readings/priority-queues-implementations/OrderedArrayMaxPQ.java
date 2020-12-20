import edu.princeton.cs.algs4.*;
import java.util.NoSuchElementException;
import java.util.Arrays;

class OrderedArrayMaxPQ<Item extends Comparable<Item>>
{
    private Item a[];
    private int N = 0;

    public OrderedArrayMaxPQ(int initialSize) {
        a = (Item[]) new Comparable[initialSize];
    }

    public void insert(Item item) {
        if (N == a.length)
            resize(a.length * 2);

        int i = N - 1;
        while (i >= 0 && less(item, a[i])) {
            a[i + 1] = a[i];
            i--;
        }

        a[i + 1] = item;
        N++;
    }

    public Item delMax() {
        if (isEmpty()) throw new NoSuchElementException();

        Item removed = a[N - 1];
        a[N - 1] = null;
        N--;
        return removed;
    }

    public boolean isEmpty() {
        return N == 0;
    }

    private boolean less(Item v, Item w) {
        return v.compareTo(w) < 0;
    }

    private void resize(int newLen) {
        Item[] copy = (Item[]) new Object[newLen];

        for (int i = 0; i < a.length; ++i) {
            copy[i] = a[i];
        }

        a = copy;
    }

    static public void main(String[] args) {
        // insert a bunch of strings
        String[] strings = {"it", "was", "the", "best", "of", "times", "it", "was", "the", "worst"};

        OrderedArrayMaxPQ<String> pq = new OrderedArrayMaxPQ<String>(strings.length);
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
        OrderedArrayMaxPQ<Integer> npq = new OrderedArrayMaxPQ<Integer>(numbers.length);
        for (int i = 0; i < numbers.length; i++) {
            npq.insert(numbers[i]);
        }

        StdOut.println("In reverse order: ");
        while (!npq.isEmpty())
            StdOut.printf("%d ", npq.delMax());

        StdOut.println();
    }
}
