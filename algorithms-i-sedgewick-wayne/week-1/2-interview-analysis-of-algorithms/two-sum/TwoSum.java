import java.util.*;
import edu.princeton.cs.algs4.*;

class TwoSum {
    public static int search(int[] nums, int target) {
        int l = 0;
        int r = nums.length - 1;

        while (l <= r) {
            int mid = l + (r - l) / 2;

            if (nums[mid] == target)
                return mid;

            if (nums[mid] < target)
                l = mid + 1;
            else
                r = mid - 1;
        }

        return -1;
    }

    public static int count(int[] a) {
        Arrays.sort(a);
        int cnt = 0;

        for (int i = 0; i < a.length; ++i) {
            if (TwoSum.search(a, -a[i]) > i) {
                cnt++;
            }
        }

        return cnt;
    }
}

/*
class TwoSum {
    public static int count(int[] a) {
        Arrays.sort(a);
        int cnt = 0;

        for (int i = 0; i < a.length; ++i) {
            for (int j = i + 1; j < a.length; ++j) {
                if (a[i] + a[j] == 0) {
                    cnt++;
                }
            }
        }

        return cnt;
    }
}
*/
