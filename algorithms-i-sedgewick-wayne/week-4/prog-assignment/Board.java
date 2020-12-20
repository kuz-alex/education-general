import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.ResizingArrayBag;
import java.lang.Math;
import java.util.Arrays;

public class Board
{
    private int[] board;
    private int[] goal;
    private int N;
    private int dimension;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        dimension = tiles.length;
        N = dimension * dimension;

        board = new int[N];
        goal = new int[N];

        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j) {
                int index = (i * dimension) + j;
                board[index] = tiles[i][j];
                goal[index] = index + 1;
            }
        }

        // array: `row = i // n; column = i % n;`
        goal[N - 1] = 0;
    }

    // string representation of this board
    public String toString() {
        String str = Integer.toString(dimension) + "\n";

        for (int i = 0; i < N; ++i) {
            str += String.format("%2d ", board[i]);
            if ((i + 1) % dimension == 0)
                str += "\n";
        }

        return str;
    }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < N - 1; ++i) {
            if (board[i] != goal[i])
                return false;
        }
        return true;
    }

    public int hamming() {
        int count = 0;
        for (int i = 0; i < N; ++i) {
            if (board[i] == 0) continue;

            if (board[i] != goal[i]) {
                // StdOut.printf("Wrong pos: %d (%d != %d)\n", i, board[i], goal[i]);
                count++;
            }
        }
        return count;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int distance = 0;

        for (int i = 0; i < N; ++i) {
            if (board[i] == 0) continue;

            int currentRow = i / dimension;
            int currentCol = i % dimension;

            int goalRow = (board[i] - 1) / dimension;
            int goalCol = (board[i] - 1) % dimension;

            // StdOut.printf("[%d]: row: %d, col: %d, goalRow: %d, goalCol: %d.\n", board[i], currentRow, currentCol, goalRow, goalCol);
            distance += Math.abs(goalRow - currentRow) + Math.abs(goalCol - currentCol);
        }

        return distance;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;

        Board that = (Board) y;

        for (int i = 0; i < this.board.length; ++i) {
            if (this.board[i] != that.board[i]) {
            }
        }

        return this.toString().compareTo(that.toString()) == 0;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        ResizingArrayBag<Board> boards = new ResizingArrayBag<Board>();

        int emptyTileRow = dimension - 1;
        int emptyTileCol = dimension - 1;

        for (int i = 0; i < N; ++i) {
            if (board[i] == 0) {
                // Found the empty tile, determine possible neighbors.
                emptyTileRow = i / dimension;
                emptyTileCol = i % dimension;
                break;
            }
        }

        // Now determine it's possible neighbors.
        if (emptyTileCol - 1 >= 0)
            boards.add(swapTiles(0, emptyTileRow, emptyTileCol - 1));
        if (emptyTileRow - 1 >= 0)
            boards.add(swapTiles(0, emptyTileRow - 1, emptyTileCol));

        if (emptyTileCol + 1 < dimension)
            boards.add(swapTiles(0, emptyTileRow, emptyTileCol + 1));
        if (emptyTileRow + 1 < dimension)
            boards.add(swapTiles(0, emptyTileRow + 1, emptyTileCol));

        return boards;
    }

    public Board twin() {
        // Swap the tiles 
        for (int i = 1; i < N; ++i) {
            if (board[i] != 0 && board[i - 1] != 0) {
                int currentRow = i / dimension;
                int currentCol = i % dimension;
                Board newBoard = swapTiles(board[i - 1], currentRow, currentCol);
                return newBoard;
            }
        }

        return null;
    }

    // Creates a new board where empty tile and tile `toRow, toCol` are swapped.
    private Board swapTiles(int targetTileValue, int toRow, int toCol) {
        int[] newBoard = Arrays.copyOf(board, N);
        int[][] newBoardTiles = new int[dimension][dimension];

        for (int i = 0; i < N; ++i) {
            int currentTile = board[i];
            int row = i / dimension;
            int col = i % dimension;

            if (currentTile == targetTileValue) {
                // We put the value from the target cell in position of the empty tile.
                newBoardTiles[row][col] = board[(toRow * dimension) + toCol];
            } else {
                newBoardTiles[row][col] = currentTile;
            }
        }

        // We put the targetValue back.
        newBoardTiles[toRow][toCol] = targetTileValue;
        return new Board(newBoardTiles);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] arr = {{8, 1, 3}, {4, 0, 2}, {7, 6, 5}};
        Board b = new Board(arr);

        StdOut.println(b);
        assert(b.hamming() == 5);
        assert(b.manhattan() == 10);

        ResizingArrayBag<Board> neighbors = (ResizingArrayBag<Board>) b.neighbors();
        assert(neighbors.size() == 4);

        int[][] a2 = {{0, 1, 3}, {4, 8, 2}, {7, 6, 5}};
        Board b2 = new Board(a2);
        neighbors = (ResizingArrayBag<Board>) b2.neighbors();
        assert(neighbors.size() == 2);

        int[][] a3 = {{1, 0, 3}, {4, 2, 5}, {7, 8, 6}};
        Board b3 = new Board(a3);
        neighbors = (ResizingArrayBag<Board>) b3.neighbors();
        assert(neighbors.size() == 3);

        StdOut.println(b3);
        StdOut.println(b3.twin());

        int[][] a4 = {{1, 0, 3}, {4, 2, 5}, {7, 8, 6}};
        Board b4 = new Board(a4);
        assert(b4.equals(b3) == true);
        assert(b3.equals(b2) == false);
        assert(b.equals(b2) == false);

        int[][] a5 = {{1, 2, 3}, {4, 5, 6}, {7, 8, 0}};
        Board b5 = new Board(a5);
        assert(b4.isGoal() == false);
        assert(b5.isGoal() == true);
        StdOut.println(b5.manhattan());
        assert(b5.manhattan() == 0);
        assert(b5.hamming() == 0);

        StdOut.println("Neighbors of board 5:");
        for (Board neighbor : b5.neighbors()) {
            StdOut.println(neighbor);
        }
    }
}
