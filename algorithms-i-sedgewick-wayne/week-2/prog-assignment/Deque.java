import edu.princeton.cs.algs4.StdOut;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item>
{
    private Node first;
    private Node last;
    private int N;

    private class Node {
        Item item;
        Node next;
        Node prev;
    }

    public Deque() {}

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    public void addFirst(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        Node oldFirst = first;
        first = new Node();
        first.item = item;
        first.next = oldFirst;

        if (isEmpty())
            last = first;
        else
            oldFirst.prev = first;

        N++;
    }

    public void addLast(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        Node oldLast = last;
        last = new Node();
        last.item = item;
        last.prev = oldLast;

        if (isEmpty())
            first = last;
        else
            oldLast.next = last;

        N++;
    }

    public Item removeFirst() {
        if (first == null) {
            throw new NoSuchElementException();
        }
        Item removed = first.item;
        first = first.next;
        if (first != null)
            first.prev = null;
        N--;
        return removed;
    }

    public Item removeLast() {
        if (last == null) {
            throw new NoSuchElementException();
        }
        Item removed = last.item;
        last = last.prev;
        if (last != null)
            last.next = null;
        N--;
        return removed;
    }

    public Iterator<Item> iterator() {
        return new ListIterator();
    }

    private class ListIterator implements Iterator<Item> {
        private Node current = first;

        public boolean hasNext() {
            return current != null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        public Item next() {
            Item item = current.item;
            current = current.next;
            return item;
        }
    }

    static public void main(String[] args) {
        Deque<Integer> deq = new Deque<>();

        StdOut.println("> Make sure deque is empty and it's size is 0 after initialization.");
        assert(deq.isEmpty() == true);
        assert(deq.size() == 0);

        StdOut.printf("> We can iterate over the deque of size: %d.\n", deq.size());
        for (Integer i : deq) {
            StdOut.printf("Iterating: %d\n", i);
        }

        StdOut.println("> We can add elements to the deque.");
        deq.addLast(3);
        deq.addLast(4);
        deq.addLast(5);
        deq.addFirst(2);
        deq.addFirst(1);
        assert(deq.size() == 5);

        StdOut.println("> We can delete elements from the deque.");
        Integer t = deq.removeFirst();
        StdOut.printf("-\t`removeLast` removes 1 (actual: %d).\n", t);
        assert(t == 1);
        t = deq.removeFirst();
        StdOut.printf("-\t`removeLast` removes 2 (actual: %d).\n", t);
        assert(t == 2);
        t = deq.removeLast();
        StdOut.printf("-\t`removeLast` removes 5 (actual: %d).\n", t);
        assert(t == 5);
        t = deq.removeFirst();
        StdOut.printf("-\t`removeLast` removes 3 (actual: %d).\n", t);
        assert(t == 3);
        t = deq.removeLast();
        StdOut.printf("-\t`removeLast` removes 4 (actual: %d).\n", t);
        assert(t == 4);
        assert(deq.size() == 0);

        StdOut.println("> We can add and remove elements in the correct order.");
        int i;
        for (i = 0; i < 100; ++i) {
            deq.addLast(i);
        }
        for (i = 0; i < 100; ++i) {
            t = deq.removeLast();
            StdOut.printf("%d ", t);
        }
        StdOut.println();

    }
}
