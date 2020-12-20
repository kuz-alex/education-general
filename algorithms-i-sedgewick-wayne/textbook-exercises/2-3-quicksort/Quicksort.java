import edu.princeton.cs.algs4.*;
import java.util.Arrays;

class Quicksort {
    public static void sort(Comparable[] a) {
        StdRandom.shuffle(a);
        sort(a, 0, a.length - 1, "");
    }

    public static void sort(Comparable[] a, int lo, int hi, String prefix) {
        if (hi <= lo) {
            return;
        }
        int j = partition(a, lo, hi);
        // System.out.printf("%spartition(a, %d, %d) = %d.\n", prefix, lo, hi, j);
        // System.out.printf("%ssort(%d, %d)\n", prefix, lo, j - 1);
        sort(a, lo, j - 1, prefix + "  ");
        // System.out.printf("%ssort(%d, %d)\n", prefix, j + 1, hi);
        sort(a, j + 1, hi, prefix + "  ");
    }

private static int partition(Comparable[] a, int lo, int hi) {
    int i = lo, j = hi + 1;
    Comparable v = a[lo];

    while (true) {
        while (less(a[++i], v))
            if (i == hi) break;
        while (less(v, a[--j]))
            if (j == lo) break;

        if (i >= j) break;
        exch(a, i, j);
    }
    exch(a, lo, j);
    return j;
}

    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    private static void exch(Comparable[] a, int i, int j) {
        Comparable t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static void main(String[] args) {
        String str = "EASYQUESTION";
        Character[] a = new Character[str.length()];
        for (int i = 0; i < str.length(); ++i) {
            a[i] = str.charAt(i);
        }

        sort(a);
        System.out.println(Arrays.toString(a));
    }
}

