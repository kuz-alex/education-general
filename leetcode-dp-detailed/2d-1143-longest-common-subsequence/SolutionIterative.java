import java.util.*;

class SolutionIterative {
    public int longestCommonSubsequence(String text1, String text2) {
        int n = text1.length();
        int m = text2.length();
        int[][] memo = new int[n + 1][m + 1];

        // memo[n][m] = 0;

        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                if (text1.charAt(i) == text2.charAt(j)) {
                    memo[i + 1][j + 1] = 1 + memo[i][j];
                } else {
                    memo[i + 1][j + 1] = Math.max(memo[i + 1][j], memo[i][j + 1]);
                }
            }
        }

        System.out.println(Arrays.deepToString(memo));
        return memo[n][m];
    }

    public static void main(String[] args) {
        System.out.println(new SolutionIterative().longestCommonSubsequence("adbace", "babce"));
        System.out.println(new SolutionIterative().longestCommonSubsequence("bd", "abd"));
    }
}

