// https://leetcode.com/problems/min-cost-climbing-stairs/
public class Solution {
    private int[] cost;
    private int[] memo;
    private int n;

    private int dp(int i) {
        if (i < 2) {
            return cost[i];
        }

        if (memo[i] != 0) {
            return memo[i];
        }

        int left = dp(i - 1);
        int right = dp(i - 2);
        memo[i] = cost[i] + Math.min(left, right);
        return memo[i];
    }

    public int minCostClimbingStairs(int[] cost) {
        this.cost = cost;
        n = cost.length;
        this.memo = new int[n];
        return Math.min(dp(n - 1), dp(n - 2));
    }

    public static void main(String[] args) {
        System.out.println("testeroni: " + new Solution().minCostClimbingStairs(new int[]{10, 15, 20}));
        System.out.println("testeroni: " + new Solution().minCostClimbingStairs(new int[]{1,100,1,1,1,100,1,1,100,1}));
    }
}