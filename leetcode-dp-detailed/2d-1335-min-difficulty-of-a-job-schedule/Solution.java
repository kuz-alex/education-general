import java.util.*;

class Solution {
    int[] jobDifficulty;
    int jobsN;
    int daysN;
    int[][] memo;

    private int dp(int i, int day) {
        if (day == daysN) {
            return getHardestJob(i, jobsN);
        }
        
        if (memo[i][day] != -1) {
            return memo[i][day];
        }

        int hardest = Integer.MIN_VALUE;
        int minDifficulty = Integer.MAX_VALUE;

        for (int j = i; j < jobsN - (daysN - day); ++j) {
            hardest = Math.max(hardest, jobDifficulty[j]);
            minDifficulty = Math.min(
                minDifficulty,
                hardest + dp(j + 1, day + 1)
            );
        }
        memo[i][day] = minDifficulty;

        
        return memo[i][day];
    }

    private int getHardestJob(int start, int end) {
        int hardest = jobDifficulty[start];
        
        for (int i = start; i < end; ++i) {
            hardest = Math.max(hardest, jobDifficulty[i]);
        }
        return hardest;
    }

    public int minDifficulty(int[] jobDifficulty, int d) {
        this.jobsN = jobDifficulty.length;
        if (d > this.jobsN) {
            return -1;
        }


        this.daysN = d;
        this.jobDifficulty = jobDifficulty;
        this.memo = new int[this.jobsN][this.daysN + 1];
        for (int i = 0; i < this.jobsN; ++i) {
            Arrays.fill(memo[i], -1);
        }

        return dp(0, 1);
    }

    public static void main(String[] args) {
        System.out.println(
            new Solution().minDifficulty(new int[]{6,5,4,3,2,1}, 2)
        );
    }
}
