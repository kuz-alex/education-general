import java.util.*;

class MergeTopDown {
    private static Comparable[] aux; // aux array for merges

    public static void sort(Comparable[] a)
    {
        aux = new Comparable[a.length]; // allocate space just once.
        sort(a, 0, a.length - 1, "");
    }

    private static void sort(Comparable[] a, int lo, int hi, String prefix)
    {
        if (hi <= lo) return;
        int mid = lo + (hi - lo) / 2;
        System.out.printf("%ssort(a, %d, %d).\n", prefix, lo, mid);
        sort(a, lo, mid, prefix + "  ");
        System.out.printf("%ssort(a, %d, %d).\n", prefix, mid + 1, hi);
        sort(a, mid + 1, hi, prefix + "  ");
        System.out.printf("%smerge(a, %d, %d, %d).\n", prefix, lo, mid, hi);
        merge(a, lo, mid, hi);
    }

    public static void merge(Comparable[] a, int lo, int mid, int hi)
    {
        int i = lo, j = mid + 1;

        for (int k = lo; k <= hi; ++k) // Copy a[lo..hi] to aux[lo..hi].
            aux[k] = a[k];

        for (int k = lo; k <= hi; ++k) { // Merge back to a[lo..hi].
            if (i > mid) { // left half exhausted
                a[k] = aux[j++];
            } else if (j > hi) { // right half exhausted
                a[k] = aux[i++];
            } else if (less(aux[j], aux[i])) {
                a[k] = aux[j++];
            } else {
                a[k] = aux[i++];
            }
        }
    }

    private static boolean less(Comparable v, Comparable w) {
        return v.compareTo(w) < 0;
    }

    public static void main(String[] args) {
        String str = "EASYQUES";
        Character[] a = new Character[str.length()];
        for (int i = 0; i < str.length(); ++i) {
            a[i] = str.charAt(i);
        }
        sort(a);
    }
}
