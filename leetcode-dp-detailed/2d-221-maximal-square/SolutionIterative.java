import java.util.*;

class SolutionIterative {
    char[][] matrix;
    
    private int dpIterative(int rowsN, int colsN) {
        int[][] dp = new int[rowsN][colsN];
        int currentMax = 0;

        for (int r = 0; r < rowsN; ++r) {
            dp[r][0] = Character.getNumericValue(matrix[r][0]);
        }
        for (int c = 0; c < colsN; ++c) {
            dp[0][c] = Character.getNumericValue(matrix[0][c]);
        }

        for (int r = 1; r < rowsN; ++r) {
            for (int c = 1; c < colsN; ++c) {
                if (
                    matrix[r][c] == '1' &&
                    matrix[r - 1][c] == '1' &&
                    matrix[r][c - 1] == '1' &&
                    matrix[r - 1][c - 1] == '1'
                ) {
                    dp[r][c] = 1 + Math.min(Math.min(dp[r - 1][c - 1], dp[r][c - 1]), dp[r - 1][c]);
                    currentMax = Math.max(currentMax, dp[r][c]);
                } else if (matrix[r][c] == '1') {
                    dp[r][c] = 1;
                } else {
                    dp[r][c] = 0;
                }
            }
        }

        return currentMax * currentMax;
    }
        
    public int maximalSquare(char[][] matrix) {
        int rowsN = matrix.length;
        int colsN = matrix[0].length;
        this.matrix = matrix;
        
        return dpIterative(rowsN, colsN);
    }

    public static void main(String[] args) {
        char[][] input = new char[][] {
            {'1','1','1','1','0'},
            {'1','1','1','1','0'},
            {'1','1','1','1','1'},
            {'1','1','1','1','1'},
            {'0','0','1','1','1'}
        };

        System.out.println(new SolutionIterative().maximalSquare(input));
    }
}
