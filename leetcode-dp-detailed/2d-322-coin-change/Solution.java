import java.util.*;

class Solution {
    int[] coins;
    int[] memo;

    private int dp(int n) {
        if (n == 0) return 0;
        if (n < 0) return -1;
        
        if (memo[n] != 0) {
            return memo[n];
        }

        int fewest = Integer.MAX_VALUE;

        for (int i = 0; i < coins.length; ++i) {
            if (n - coins[i] < 0) {
                continue;
            }
            int result = dp(n - coins[i]);
            if (result != -1) {
                fewest = Math.min(fewest, result);
            }
        }
        
        if (fewest == Integer.MAX_VALUE) {
            memo[n] = -1;
        } else {
            memo[n] = 1 + fewest;
        }
        
        return memo[n];
    }
    
    public int coinChange(int[] coins, int amount) {
        this.coins = coins;
        this.memo = new int[amount + 1];

        return dp(amount);
    }

    public static void main(String[] args) {
        System.out.println(new Solution().coinChange(new int[]{1,2,5}, 11));
        System.out.println(new Solution().coinChange(new int[]{2}, 3));
        System.out.println(new Solution().coinChange(new int[]{1}, 0));
        System.out.println(new Solution().coinChange(new int[]{186, 419, 83, 408}, 6249));
        System.out.println(new Solution().coinChange(new int[]{1, 10, 50}, 114));
    }
}

