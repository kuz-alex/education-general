import java.util.*;

/**
 * 188. Best Time to Buy and Sell Stock IV
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/
 */
class Solution {
    private int[] prices;
    private int[][][] memo;

    private int dp(int i, int isHolding, int k) {
        if (i == prices.length) {
            return 0;
        }

        if (isHolding == 0 && k == 0) {
            // Nothing to sell and can't buy anymore, just skip
            return 0;
        }

        if (memo[k][isHolding][i] != -1) {
            return memo[k][isHolding][i];
        }

        int totalProfit = 0;

        if (isHolding == 1) {
            totalProfit += Math.max(
                // Sell and get profit
                prices[i] + dp(i + 1, 0, k),
                // Don't sell and just keep going
                dp(i + 1, 1, k)
            );
        } else {
            totalProfit += Math.max(
                // Buy
                dp(i + 1, 1, k - 1) - prices[i],
                // Or just skip
                dp(i + 1, 0, k)
            );
        }

        memo[k][isHolding][i] = totalProfit;
        return totalProfit;
    }

    public int maxProfit(int k, int[] prices) {
        this.prices = prices;
        this.memo = new int[k + 1][2][prices.length];

        for (int i = 0; i <= k; ++i) {
            Arrays.fill(memo[i][0], -1);
            Arrays.fill(memo[i][1], -1);
        }

        int result = dp(0, 0, k);
        System.out.println(Arrays.deepToString(this.memo));
        return result;
    }

    public static void main(String[] args) {
        System.out.println(new Solution().maxProfit(2, new int[]{2, 4, 1}));
        System.out.println(new Solution().maxProfit(2, new int[]{3,2,6,5,0,3}));
    }
}
