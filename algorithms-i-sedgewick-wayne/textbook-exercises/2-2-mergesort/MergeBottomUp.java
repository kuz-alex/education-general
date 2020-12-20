import java.util.*;

class MergeBottomUp {
    private static Comparable[] aux; // aux array for merges

    public static void sort(Comparable[] a)
    {
        int N = a.length;
        aux = new Comparable[N];

        for (int sz = 1; sz < N; sz = sz + sz) {
            String prefix = "";
            for (int lo = 0; lo < N - sz; lo += sz + sz) {
                System.out.printf("%smerge(a, %d, %d, %d) // sz = %d.\n", prefix, lo, lo + sz - 1, Math.min(lo + sz + sz - 1, N - 1), sz);
                merge(a, lo, lo + sz - 1, Math.min(lo + sz + sz - 1, N - 1));
            }
            prefix = prefix + "  ";
        }
    }

    public static void merge(Comparable[] a, int lo, int mid, int hi)
    {
        // Merge a[lo..mid] with a[mid+1..hi]
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
        String str = "EASYQUESTION";
        Character[] a = new Character[str.length()];
        for (int i = 0; i < str.length(); ++i) {
            a[i] = str.charAt(i);
        }
        sort(a);
        System.out.println(Arrays.toString(a));
    }
}
