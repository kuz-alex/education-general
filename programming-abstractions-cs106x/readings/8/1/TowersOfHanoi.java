import java.util.*;

public class TowersOfHanoi {
    private static final int NUM_PEGS = 3;

    private static void moveSingleDisk(List<Deque<Integer>> pegs, int from, int to) {
        int disk = pegs.get(from).removeFirst();
        pegs.get(to).addFirst(disk);
        System.out.printf("Moving disk: %d from peg %d to %d.\n", disk, from, to);
    }

    private static void computeSolutionSteps(int numRingsToMove,
                                             List<Deque<Integer>> pegs,
                                             int start, int finish,
                                             int temp) {
        if (numRingsToMove == 1) {
            moveSingleDisk(pegs, start, finish);
        } else {
            computeSolutionSteps(numRingsToMove - 1, pegs, start, temp, finish);
            moveSingleDisk(pegs, start, finish);
            computeSolutionSteps(numRingsToMove - 1, pegs, temp, finish, start);
        }
    }

    private static void computeTowers(int numRings) {
        List<Deque<Integer>> pegs = new ArrayList<>();

        for (int i = 0; i < NUM_PEGS; ++i)
            pegs.add(new LinkedList<Integer>());

        for (int i = numRings; i >= 1; --i)
            pegs.get(0).addFirst(i);

        System.out.printf("Pegs before: %s %s %s.\n", pegs.get(0), pegs.get(1), pegs.get(2));
        computeSolutionSteps(numRings, pegs, 0, 1, 2);
        System.out.printf("Pegs after: %s %s %s.\n", pegs.get(0), pegs.get(1), pegs.get(2));

    }

    public static void main(String[] args) {
        computeTowers(args.length > 0 ? Integer.parseInt(args[0]) : 3);
    }
}
