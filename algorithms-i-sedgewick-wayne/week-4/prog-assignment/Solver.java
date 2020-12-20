import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.LinkedStack;
import edu.princeton.cs.algs4.MinPQ;
import java.lang.Math;
import java.util.Arrays;
import java.util.Comparator;

public class Solver {
    private SearchNode lastNodeInSolution;
    private Board initial;

    private class SearchNode
    {
        public Board board;
        public int movesCount;
        public SearchNode prev;
        public int priority;

        public SearchNode(Board board, SearchNode prev) {
            this.board = board;
            this.movesCount = prev == null ? 0 : prev.movesCount + 1;
            this.prev = prev;
            this.priority = board.manhattan() + this.movesCount;
        }

        public String toString() {
            String str = board.toString();
            str += String.format("Moves count: %d.\n", movesCount);
            return str;
        }
    }

    public Solver(Board initial) {
        if (initial == null)
            throw new IllegalArgumentException();

        this.initial = initial;

        PriorityOrder priorityOrder = new PriorityOrder();
        MinPQ<SearchNode> mainPQ = new MinPQ<SearchNode>(priorityOrder);
        MinPQ<SearchNode> twinPQ = new MinPQ<SearchNode>(priorityOrder);

        SearchNode main = new SearchNode(initial, null);
        SearchNode twin = new SearchNode(initial.twin(), null);
        mainPQ.insert(main);
        twinPQ.insert(twin);

        while (true) {
            main = mainPQ.delMin();
            twin = twinPQ.delMin();

            if (main.board.isGoal()) {
                lastNodeInSolution = main;
                break;
            }
            if (twin.board.isGoal()) {
                lastNodeInSolution = null;
                break;
            }

            for (Board mainNeighbor : main.board.neighbors()) {
                if (main.prev == null || !main.prev.board.equals(mainNeighbor))
                    mainPQ.insert(new SearchNode(mainNeighbor, main));
            }
            for (Board twinNeighbor : twin.board.neighbors()) {
                if (twin.prev == null || !twin.prev.board.equals(twinNeighbor))
                    twinPQ.insert(new SearchNode(twinNeighbor, twin));
            }
        }
    }

    public boolean isSolvable() {
        return lastNodeInSolution != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return lastNodeInSolution == null ? -1 : lastNodeInSolution.movesCount;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (lastNodeInSolution == null) return null;

        SearchNode current = lastNodeInSolution;
        LinkedStack<Board> s = new LinkedStack<Board>();

        while (current.movesCount != 0) {
            s.push(current.board);
            current = current.prev;
        }

        s.push(current.board);
        return s;
    }

    private class PriorityOrder implements Comparator<SearchNode>
    {
        public int compare(SearchNode v, SearchNode w) {
            return Integer.compare(v.priority, w.priority);
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();

        Board initial = new Board(tiles);
        Solver solver = new Solver(initial);

        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
