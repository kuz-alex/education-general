import java.util.*;

class Node {
    int value;
    Node left;
    Node right;
    Node(int value) {
        this.value = value;
        left = right = null;
    }

    Node(int value, Node left, Node right) {
        this.value = value;
        this.right = right;
        this.left = left;
    }
}

class Web_Exercise1_GreatTree {
    private static void join(Node a, Node b) {
        a.right = b;
        b.left = a;
    }

    private static Node concat(Node a, Node b) {
        if (a == null) return b;
        if (b == null) return a;

        Node tailA = a.left;
        Node tailB = b.left;

        join(tailA, b);
        join(tailB, a);

        return a;
    }

    public static Node toList(Node root) {
        if (root == null) {
            return root;
        }

        Node aList = toList(root.left);
        Node bList = toList(root.right);

        // Prepare root for merging.
        root.left = root;
        root.right = root;

        Node start = concat(aList, root);
        start = concat(aList, bList);
        return start;
    }

    private static List<Integer> printTree(Node root) {
        if (root == null) {
            return new ArrayList<Integer>();
        }

        List<Integer> res = new ArrayList<>();

        List<Integer> l = printTree(root.left);
        for (int i : l)
            res.add(i);

        res.add(root.value);

        l = printTree(root.right);

        for (int i : l)
            res.add(i);

        return res;
    }

    private static void printList(Node head) {
        Node current = head;

        while (current != null) {
            System.out.printf("%d ", current.value);
            current = current.right;
            if (current == head) break;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Node root = new Node(4);
        root.left = new Node(2);
        root.right = new Node(5);
        root.left.left = new Node(1);
        root.left.right = new Node(3);
        System.out.println(printTree(root));

        Node head = toList(root);
        System.out.print("List: ");
        printList(head);
    }
}
