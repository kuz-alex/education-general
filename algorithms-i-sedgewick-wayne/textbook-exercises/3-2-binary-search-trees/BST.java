package chapter3.section2;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Queue;

class BST<Key extends Comparable<Key>, Value> {
    private Node root;

    private class Node {
        private Key key;
        private Value val;
        private Node left, right;
        private int N;

        public Node(Key key, Value val, int N) {
            this.key = key; this.val = val; this.N = N;
        }
    }

    // Size.
    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        else           return x.N;
    }

    // Get.
    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (x == null) return null;

        int cmp = key.compareTo(x.key);
        if (cmp < 0) return get(x.left, key);
        else if (cmp > 0) return get(x.right, key);
        else return x.val;
    }

    // Put.
    public void put(Key key, Value val) {
        root = put(root, key, val);
    }

    private Node put(Node x, Key key, Value val) {
        if (x == null) return new Node(key, val, 1);

        int cmp = key.compareTo(x.key);
        if      (cmp < 0) x.left  = put(x.left, key, val);
        else if (cmp > 0) x.right = put(x.right, key, val);
        else x.val = val;
        x.N = 1 + size(x.left) + size(x.right);
        return x;
    }

    // Min.
    public Key min() {
        return min(root).key;
    }

    private Node min(Node x) {
        if (x.left == null) return x;
        return min(x.left);
    }

    // Max
    public Key max() {
        return max(root).key;
    }

    private Node max(Node x) {
        if (x.right == null) return x;
        return max(x.right);
    }

    // Floor.
    public Key floor(Key key) {
        Node x = floor(root, key);
        if (x == null) return null;
        return x.key;
    }

    private Node floor(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp < 0) return floor(x.left, key);

        Node t = floor(x.right, key);
        if (t != null) return t;
        else           return x;
    }

    // Ceiling.
    public Key ceiling(Key key) {
        Node x = ceiling(root, key);
        if (x == null) return null;
        return x.key;
    }

    private Node ceiling(Node x, Key key) {
        if (x == null) return null;

        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp > 0) return ceiling(x.right, key);

        Node t = ceiling(x.left, key);
        if (t != null) return t;
        else           return x;
    }

    // Keys.
    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    public Iterable<Key> keys(Key lo, Key hi) {
        Queue<Key> queue = new Queue<Key>();
        keys(root, queue, lo, hi);
        return queue;
    }

    private void keys(Node x, Queue<Key> queue, Key lo, Key hi) {
        if (x == null) return;
        int cmplo = lo.compareTo(x.key);
        int cmphi = hi.compareTo(x.key);
        if (cmplo < 0) keys(x.left, queue, lo, hi);
        if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.key);
        if (cmphi > 0) keys(x.right, queue, lo, hi);
    }

    // Delete.
    public Node delete(Key key) {
        return delete(root, key);
    }

    private Node delete(Node x, Key key) {
        if (x == null) return null;

        int cmp = key.compareTo(x.key);
        if (cmp > 0) x.right = delete(x.right, key);
        else if (cmp < 0) x.left = delete(x.left, key);
        else {
            if (x.right == null) return x.left;
            if (x.left == null) return x.right;

            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }

        x.N = 1 + size(x.right) + size(x.left);
        return x;
    }

    // Delete min.
    public void deleteMin() {
        root = deleteMin(root);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.N = 1 + size(x.left) + size(x.right);
        return x;
    }

    // Delete max.
    public void deleteMax() {
        root = deleteMax(root);
    }

    private Node deleteMax(Node x) {
        if (x.right == null) return x.left;

        x.right = deleteMax(x.right);
        x.N = 1 + size(x.left) + size(x.right);
        return x;
    }

    // Select.
    public Key select(int k) {
        return select(root, k).key;
    }

    private Node select(Node x, int k) {
        if (x == null) return null;

        int t = size(x.left);
        if      (t > k) return select(x.left, k);
        else if (t < k) return select(x.right, k - t - 1);
        else return x;
    }

    // Rank.
    public int rank(Key key) {
        return rank(root, key);
    }

    private int rank(Node x, Key key) {
        if (x == null) return 0;

        int cmp = key.compareTo(x.key);
        if (cmp < 0) return rank(x.left, key);
        else if (cmp < 0) return size(x.left) + 1 + rank(x.right, key);
        else return size(x.left);
    }

    public static void main(String[] args) {
        BST<Integer, String> bst = new BST<>();
        // Test put()
        bst.put(5, "Value 5");
        bst.put(1, "Value 1");
        bst.put(9, "Value 9");
        bst.put(2, "Value 2");
        bst.put(0, "Value 0");
        bst.put(99, "Value 99");

        StdOut.println();

        // Test size()
        StdOut.println("Size: " + bst.size() + " Expected: 6");

        // Test get() and keys()
        for (Integer key : bst.keys()) {
            StdOut.println("Key " + key + ": " + bst.get(key));
        }

        // Test min()
        StdOut.println("Min key: " + bst.min() + " Expected: 0");

        // Test max()
        StdOut.println("Max key: " + bst.max() + " Expected: 99");

        //Test floor()
        StdOut.println("Floor of 5: " + bst.floor(5) + " Expected: 5");
        StdOut.println("Floor of 15: " + bst.floor(15) + " Expected: 9");

        //Test ceiling()
        StdOut.println("Ceiling of 5: " + bst.ceiling(5) + " Expected: 5");
        StdOut.println("Ceiling of 15: " + bst.ceiling(15) + " Expected: 99");

        // Test delete()
        StdOut.println("\nDelete key 2");
        bst.delete(2);
        for (Integer key : bst.keys()) {
            StdOut.println("Key " + key + ": " + bst.get(key));
        }
        StdOut.println();

        // Test size()
        StdOut.println("Size: " + bst.size() + " Expected: 5");

        //Test select()
        StdOut.println("Select key of rank 4: " + bst.select(4) + " Expected: 99");

        //Test rank()
        StdOut.println("Rank of key 9: " + bst.rank(9) + " Expected: 3");
        StdOut.println("Rank of key 10: " + bst.rank(10) + " Expected: 4");

        //Test deleteMin()
        StdOut.println("\nDelete min (key 0)");

        bst.deleteMin();
        for(Integer key : bst.keys())
            StdOut.println("Key " + key + ": " + bst.get(key));

        //Test deleteMax()
        StdOut.println("\nDelete max (key 99)");

        bst.deleteMax();
        for(Integer key : bst.keys())
            StdOut.println("Key " + key + ": " + bst.get(key));

        //Test keys() with range
        StdOut.println();
        StdOut.println("Keys in range [2, 10]");
        for(Integer key : bst.keys(2, 10))
            StdOut.println("Key " + key + ": " + bst.get(key));

        StdOut.println("Size: " + bst.size() + " Expected: 3");
    }
}
