import java.util.*;

/**
 * 309. Best Time to Buy and Sell Stock with Cooldown
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/
 */
class Solution {
    int[] a;
    int[][][] memo;
    
    private int dp(int i, int isHolding, int isCooldown) {
        if (i >= a.length) {
            return 0;
        }

        if (isCooldown == 1) {
            // Cannot buy when on cooldown, only skip.
            return dp(i + 1, isHolding, 0);
        }

        if (memo[i][isHolding][isCooldown] != -1) {
            return memo[i][isHolding][isCooldown];
        }
        
        int result;
        if (isHolding == 1) {
            result = Math.max(
                a[i] + dp(i + 1, 0, 1), // sell
                dp(i + 1, 1, 0) // skip
            );
        } else {
            result = Math.max(
                (-a[i]) + dp(i + 1, 1, 0), // buy
                dp(i + 1, 0, 0) // skip
            );
        }
        memo[i][isHolding][isCooldown] = result;
        return result;
    }

    public int maxProfit(int[] prices) {
        a = prices;
        memo = new int[prices.length][2][2];

        for (int i = 0; i < prices.length; ++i) {
            for (int j = 0; j < 2; ++j) {
                for (int k = 0; k < 2; ++k) {
                    memo[i][j][k] = -1;
                }
            }
        }

        return dp(0, 0, 0);
    }

    public static void main(String[] args) {
        System.out.println(new Solution().maxProfit(new int[]{1,2,3,0,2}));
        System.out.println(new Solution().maxProfit(new int[]{1}));
        System.out.println(new Solution().maxProfit(new int[]{70, 4, 83, 56, 94, 72, 78, 43, 2, 86, 65, 100, 94, 56, 41, 66, 3, 33, 10, 3, 45, 94, 15, 12, 78, 60, 58, 0, 58, 15, 21, 7, 11, 41, 12, 96, 83, 77, 47}));
    }
}

