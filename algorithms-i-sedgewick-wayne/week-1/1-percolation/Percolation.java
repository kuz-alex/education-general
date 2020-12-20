import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private int[][] grid;
    private int size;
    private int setSize;
    private int count;
    private WeightedQuickUnionUF set;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }

        size = n;
        setSize = (n * n) + 2;
        grid = new int[n][n];
        set = new WeightedQuickUnionUF(setSize);
        for (int i = 0; i < n; ++i) {
            set.union(convert(0, i), 0); // connect first node to the first row.
            set.union(convert(n - 1, i), setSize - 1); // connect last node to the last row.
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        this.validateIndicies(row, col);

        row = row - 1;
        col = col - 1;

        grid[row][col] = 1;

        // Check top and bottom adjacent cells to connect open sites.
        if (row - 1 >= 0) {
            if (grid[row - 1][col] == 1) {
                set.union(convert(row, col), convert(row - 1, col));
            }
        }
        if (row + 1 < size) {
            if (grid[row + 1][col] == 1) {
                set.union(convert(row, col), convert(row + 1, col));
            }
        }

        // Check right and left adjacent cells to connect open sites.
        if (col - 1 >= 0) {
            if (grid[row][col - 1] == 1) {
                set.union(convert(row, col), convert(row, col - 1));
            }
        }
        if (col + 1 < size) {
            if (grid[row][col + 1] == 1) {
                set.union(convert(row, col), convert(row, col + 1));
            }
        }

        ++count;
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        this.validateIndicies(row, col);

        row = row - 1;
        col = col - 1;

        return grid[row][col] == 1;
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        this.validateIndicies(row, col);

        row = row - 1;
        col = col - 1;

        if (grid[row][col] == 0) {
            return false;
        }

        return set.find(0) == set.find(convert(row, col));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return count;
    }

    // does the system percolate?
    public boolean percolates() {
        return set.find(setSize - 1) == set.find(0);
    }

    private void validateIndicies(int row, int col) {
        if (row > size || row < 1 || col > size || col < 1) {
            throw new IllegalArgumentException();
        }
    }

    private int convert(int row, int col) {
        return ((row * size) + col) + 1;
    }

    // test client (optional)
    public static void main(String[] args) {
        Percolation p = new Percolation(2);

        assert(p.isOpen(1, 1) == false);
        assert(p.isOpen(1, 2) == false);
        assert(p.isOpen(2, 1) == false);
        assert(p.isOpen(2, 2) == false);

        assert(p.isFull(1, 1) == false);
        assert(p.isFull(1, 2) == false);
        assert(p.isFull(2, 1) == false);
        assert(p.isFull(2, 2) == false);

        p.open(1,1);
        assert p.isOpen(1,1) == true : "should open (1,1) site";
        assert p.isFull(1,1) == true : "site (1,1) should be full";
        assert(p.percolates() == false);

        p.open(2,1);
        assert p.isOpen(2,1) == true : "should open (2,1) site";
        assert p.percolates() == true : "should percolate";
    }
}

