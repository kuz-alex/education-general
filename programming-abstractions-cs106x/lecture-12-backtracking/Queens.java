import java.util.*;

/*
[
 [".Q..",  // Solution 1
  "...Q",
  "Q...",
  "..Q."],

 ["..Q.",  // Solution 2
  "Q...",
  "...Q",
  ".Q.."]
]
 */
class Queens {
    static private class Board {
        private int n;
        private boolean[][] state;

        public Board(int n) {
            this.n = n;
            state = new boolean[n][];
            for (int i = 0; i < n; ++i) {
                state[i] = new boolean[n];
            }
        }

        public boolean isSafe(int row, int col) {
            if (state[col][row] == true)
                return false;

            // Make sure no queens on vertical line
            for (int i = 0; i < n; ++i) {
                if (i != row && state[i][col] == true)
                    return false;
            }
            // Make sure no queens on vertical line
            for (int i = 0; i < n; ++i) {
                if (i != col && state[row][i])
                    return false;
            }

            // Check diagonals
            for (int i = row, j = col; i >= 0 && j >= 0; --i, --j)
                if (state[i][j] == true)
                    return false;

            for (int i = row, j = col; i < n && j < n; ++i, ++j)
                if (state[i][j] == true)
                    return false;

            for (int i = row, j = col; i < n && j >= 0; ++i, --j)
                if (state[i][j] == true)
                    return false;

            for (int i = row, j = col; i >= 0 && j < n; --i, ++j)
                if (state[i][j] == true)
                    return false;

            return true;
        }

        public int size() {
            return n;
        }

        public void place(int col, int row) {
            state[col][row] = true;
        }

        public void remove(int col, int row) {
            state[col][row] = false;
        }

        public List<String> stringRepresentation() {
            List<String> result = new ArrayList<>();

            for (int i = 0; i < n; ++i) {
                String s = "\n";
                for (int j = 0; j < n; ++j)
                    s += state[i][j] == true ? "Q" : ".";
                result.add(s);
            }

            return result;
        }
    }
        
    public static List<List<String>> solveNQueens(int n) {
        List<List<String>> solutions = new ArrayList<>();
        backtrack(new Board(n), 0, solutions);
        return solutions;
    }
    
    private static void backtrack(Board board, int col, List<List<String>> solutions) {
        if (col == board.size()) {
            solutions.add(board.stringRepresentation());
            return;
        } else {
            for (int i = 0; i < board.size(); ++i) {
                if (board.isSafe(i, col)) {
                    board.place(i, col);
                    backtrack(board, col + 1, solutions);
                    board.remove(i, col);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Test Board class.
        Board b = new Board(4);

        b.place(0, 0);
        // Cannot place a queen in the occupied square.
        assert(b.isSafe(0, 0) == false);
        // Vertical
        assert(b.isSafe(0, 1) == false);
        // Horizontal
        assert(b.isSafe(1, 0) == false);
        assert(b.isSafe(2, 0) == false);

        b.remove(0, 0);
        assert(b.isSafe(0, 0) == true);

        b.place(1, 2);
        assert(b.isSafe(0, 1) == false);
        assert(b.isSafe(0, 3) == false);
        assert(b.isSafe(2, 3) == false);
        assert(b.isSafe(3, 0) == false);
        assert(b.isSafe(3, 1) == true);
        assert(b.isSafe(2, 1) == false);

        //System.out.println(b.stringRepresentation());
        System.out.println(solveNQueens(4));
    }
}
