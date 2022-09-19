import java.util.*;

/**
 * 122. Best Time to Buy and Sell Stock II
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-ii/
 */
class Solution {
    private int[] prices;
    private int[][] memo;

    private int dp(int i, int isHolding) {
        if (i == prices.length) {
            return 0;
        }

        if (memo[isHolding][i] != -1) {
            return memo[isHolding][i];
        }

        int totalProfit = 0;
        int current = prices[i];

        if (isHolding == 1) {
            totalProfit += Math.max(
                // Sell and get profit
                prices[i] + dp(i + 1, 0),
                // Don't sell and just keep going
                dp(i + 1, 1)
            );
        } else {
            totalProfit += Math.max(
                // Buy
                dp(i + 1, 1) - prices[i],
                // Or just skip
                dp(i + 1, 0)
            );
        }

        memo[isHolding][i] = totalProfit;
        return totalProfit;
    }

    public int maxProfit(int[] prices) {
        this.prices = prices;
        this.memo = new int[2][prices.length];
        Arrays.fill(memo[0], -1);
        Arrays.fill(memo[1], -1);

        int result = dp(0, 0);
        System.out.println(Arrays.deepToString(this.memo));
        return result;
    }

    public static void main(String[] args) {
        System.out.println(new Solution().maxProfit(new int[]{7,1,5,3,6,4}));
        System.out.println(new Solution().maxProfit(new int[]{1,2,3,4,5}));
        System.out.println(new Solution().maxProfit(new int[]{7,6,4,3,1}));
        System.out.println(new Solution().maxProfit(new int[]{7,6,1,2,3,4,5,43,3,1}));
    }
}
