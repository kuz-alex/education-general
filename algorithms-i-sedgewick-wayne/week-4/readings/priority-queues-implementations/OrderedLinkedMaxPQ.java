import edu.princeton.cs.algs4.*;
import java.util.NoSuchElementException;

class OrderedLinkedMaxPQ<Key extends Comparable<Key>> {
    int n = 0;
    Node head;

    private class Node {
        Key key;
        Node next;
        Node prev;
    }

    public OrderedLinkedMaxPQ() {}

    public void insert(Key key) {
        Node newNode = new Node();
        newNode.key = key;

        if (isEmpty()) {
            head = newNode;
            n++;
            return;
        }

        n++;

        if (less(head.key, key)) {
            Node oldHead = head;
            head = newNode;
            head.next = oldHead;
            oldHead.prev = head;
            return;
        }

        Node firstSmallerNode = null;
        Node current = null;

        for (current = head; current.next != null; current = current.next) {
            if (less(current.next.key, key)) {
                firstSmallerNode = current.next;
                break;
            }
        }

        if (firstSmallerNode == null) {
            Node oldTail = current;
            oldTail.next = newNode;
            newNode.prev = oldTail;
        } else {
            Node oldPrev = firstSmallerNode.prev;
            newNode.prev = oldPrev;
            newNode.next = firstSmallerNode;
            oldPrev.next = newNode;
            firstSmallerNode.prev = newNode;
        }
    }

    public Key delMax() {
        if (isEmpty()) throw new NoSuchElementException();
        Key removed = head.key;
        if (head.next != null) {
            head = head.next;
            head.prev = null;
        } else {
            head = null;
        }
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

        OrderedLinkedMaxPQ<String> pq = new OrderedLinkedMaxPQ<String>();
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
        OrderedLinkedMaxPQ<Integer> npq = new OrderedLinkedMaxPQ<Integer>();
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
