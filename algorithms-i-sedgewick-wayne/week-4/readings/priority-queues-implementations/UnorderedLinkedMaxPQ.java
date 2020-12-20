import edu.princeton.cs.algs4.*;
import java.util.NoSuchElementException;

class UnorderedLinkedMaxPQ<Key extends Comparable<Key>> {
    int n = 0;
    Node head;

    private class Node {
        Key key;
        Node next;
        Node prev;
    }

    public UnorderedLinkedMaxPQ() {}

    public void insert(Key key) {
        Node oldHead = head;
        head = new Node();
        head.key = key;
        head.next = oldHead;
        if (oldHead != null)
            oldHead.prev = head;
        n++;
    }

    public Key delMax() {
        if (isEmpty()) throw new NoSuchElementException();

        Node max = head;
        for (Node current = head; current.next != null; current = current.next) {
            if (less(max.key, current.key))
                max = current;
        }

        Key removed = max.key;

        if (max.next != null)
            max.next.prev = max.prev;

        if (max.prev != null)
            max.prev.next = max.next;
        else
            head = max.next;

        n--;
        return removed;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    private boolean less(Key v, Key w) {
        return v.compareTo(w) < 0;
    }

    static public void main(String[] args) {
        // insert a bunch of strings
        String[] strings = {"it", "was", "the", "best", "of", "times", "it", "was", "the", "worst"};

        UnorderedLinkedMaxPQ<String> pq = new UnorderedLinkedMaxPQ<String>();
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
        UnorderedLinkedMaxPQ<Integer> npq = new UnorderedLinkedMaxPQ<Integer>();
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
