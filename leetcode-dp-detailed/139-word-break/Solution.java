import java.util.*;

/**
 * 139. Word Break
 * https://leetcode.com/problems/word-break/
 */
class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        boolean dp[] = new boolean[s.length()];
        
        for (int i = 0; i < s.length(); ++i) {
            for (String word : wordDict) {
                if (i >= word.length() - 1 && (i == word.length() - 1 || dp[i - word.length()])) {
                    if (s.substring(i - word.length() + 1, i + 1).equals(word)) {
                        dp[i] = true;
                        break;
                    }
                }
            }
        }

        System.out.println(Arrays.toString(dp));
        return dp[s.length() - 1];
    }

    public static void main(String[] args) {
        System.out.println(new Solution().wordBreak("leetcode", Arrays.asList("leet", "code")));
        System.out.println(new Solution().wordBreak("applepenapple", Arrays.asList("apple", "pen")));
    }
}

