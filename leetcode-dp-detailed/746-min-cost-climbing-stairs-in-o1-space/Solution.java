import java.util.*;

/**
 * 746. Min Cost Climbing Stairs
 * https://leetcode.com/problems/min-cost-climbing-stairs/
 */
class Solution {
    public int minCostClimbingStairs(int[] cost) {
        int n = cost.length;
        if (n < 2) {
            return cost[n];
        }

        int[] dp = new int[n + 1];

        dp[0] = cost[0];
        dp[1] = cost[1];

        for (int i = 2; i <= n; ++i) {
            int curr = i == n ? 0 : cost[i];
            int left = dp[i - 2] + curr;
            int right = dp[i - 1] + curr;

            dp[i] = Math.min(left, right);
        }

        return dp[n];
    }

    public int minCostClimbingStairsLessSpace(int[] cost) {
        int n = cost.length;
        if (n < 2) {
            return cost[n];
        }

        int two_back = cost[0];
        int one_back = cost[1];

        for (int i = 2; i <= n; ++i) {
            int curr = i == n ? 0 : cost[i];
            int result = Math.min(
                one_back + curr,
                two_back + curr
            );

            two_back = one_back;
            one_back = result;
        }

        return Math.min(one_back, two_back);
    }

    // Уменьшение размера используемой памяти на примере Фибоначчи
    public int fibo(int n) {
        if (n <= 1) return 0;

        int one_back = 1;
        int two_back = 0;

        for (int i = 2; i <= n; ++i) {
            int temp = one_back;
            one_back = one_back + two_back;
            two_back = temp;
        }

        return one_back;
    }

    public static void main(String args[]) {
        System.out.println(new Solution().fibo(9));
        System.out.println(new Solution().minCostClimbingStairsLessSpace(new int[]{10,15,20}));
        System.out.println(new Solution().minCostClimbingStairsLessSpace(new int[]{1,100,1,1,1,100,1,1,100,1}));
    }
}
