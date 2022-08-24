import java.util.*;

class Solution {
    public int lengthOfLIS(int[] nums) {
        int n = nums.length;
        int[] dp = new int[n];
        
        Arrays.fill(dp, 1);
        
        for (int i = 1; i < n; ++i) {
            int curr = nums[i];
            for (int j = 0; j < i; ++j) {
                if (curr > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }

        int result = dp[0];
        for (int num : dp) {
            result = Math.max(result, num);
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(new Solution().lengthOfLIS(new int[]{0,1,0,3,2,3}));
        System.out.println(new Solution().lengthOfLIS(new int[]{10,9,2,5,3,7,101,18}));
        System.out.println(new Solution().lengthOfLIS(new int[]{7,7,7,7,7,7,7}));
        System.out.println(new Solution().lengthOfLIS(new int[]{1,3,6,7,9,4,10,5,6}));
    }
}
