import java.util.*;

class Solution {
    private int[][] memo;
    private String text1, text2;
    private int n, m;

    private int dp(int i, int j) {
        if (i == n || j == m) {
            return 0;
        } else if (text1.charAt(i) == text2.charAt(j)) {
            memo[i][j] = 1 + dp(i + 1, j + 1);
            return memo[i][j];
        }

        if (memo[i][j] == 0) {
            memo[i][j] = Math.max(dp(i + 1, j), dp(i, j + 1));
        }
        return memo[i][j];
    }

    public int longestCommonSubsequence(String text1, String text2) {
        n = text1.length();
        m = text2.length();
        this.text1 = text1;
        this.text2 = text2;

        this.memo = new int[n][m];
        int result = dp(0, 0);
        System.out.println(Arrays.deepToString(this.memo));
        return result;
    }

    public static void main(String[] args) {
        System.out.println(new Solution().longestCommonSubsequence("adbace", "babce"));
        System.out.println(new Solution().longestCommonSubsequence("bd", "abd"));
    }
}
