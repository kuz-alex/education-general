import edu.princeton.cs.algs4.*;

class PrintBinary {
    private static void printBinary(int digits, String prefix) {
        if (digits == 0) {
            StdOut.println(prefix);
        } else {
            printBinary(digits - 1, prefix + "0");
            printBinary(digits - 1, prefix + "1");
        }
    }

    public static void main(String[] args) {
        printBinary(2, "");
    }
}
