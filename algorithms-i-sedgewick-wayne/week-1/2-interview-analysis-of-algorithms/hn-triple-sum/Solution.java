import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;
import java.util.stream.*;

public class Solution {
    static int findFirstLarge(int []a, int q) {
        int l = 0;
        int r = a.length;

        while (l < r) {
            int mid = l + (r - l) / 2;

            if (a[mid] > q) {
                r = mid;
            } else {
                l = mid + 1;
            }
        }

        return l;
    }

    // Complete the triplets function below.
    static long triplets(int[] a, int[] b, int[] c) {
        long cnt = 0;
        a = Arrays.stream(a).distinct().toArray();
        Arrays.sort(a);
        b = Arrays.stream(b).distinct().toArray();
        Arrays.sort(b);
        c = Arrays.stream(c).distinct().toArray();
        Arrays.sort(c);

        for (int i = 0; i < b.length; ++i) {
            int maxJ = Solution.findFirstLarge(a, b[i]);
            int maxK = Solution.findFirstLarge(c, b[i]);

            cnt += maxJ * maxK;
        }

        return cnt;
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        String[] lenaLenbLenc = scanner.nextLine().split(" ");

        int lena = Integer.parseInt(lenaLenbLenc[0]);

        int lenb = Integer.parseInt(lenaLenbLenc[1]);

        int lenc = Integer.parseInt(lenaLenbLenc[2]);

        int[] arra = new int[lena];

        String[] arraItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < lena; i++) {
            int arraItem = Integer.parseInt(arraItems[i]);
            arra[i] = arraItem;
        }

        int[] arrb = new int[lenb];

        String[] arrbItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < lenb; i++) {
            int arrbItem = Integer.parseInt(arrbItems[i]);
            arrb[i] = arrbItem;
        }

        int[] arrc = new int[lenc];

        String[] arrcItems = scanner.nextLine().split(" ");
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        for (int i = 0; i < lenc; i++) {
            int arrcItem = Integer.parseInt(arrcItems[i]);
            arrc[i] = arrcItem;
        }

        long ans = triplets(arra, arrb, arrc);

        System.out.println(String.valueOf(ans));

        scanner.close();
    }
}
