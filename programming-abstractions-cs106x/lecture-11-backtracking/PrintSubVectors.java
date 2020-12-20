import java.util.*;

class PrintSubVectors {
    private static void printSubVectors(Integer[] a) {
        printSubVectorsHelper(new ArrayList<Integer>(Arrays.asList(a)), new ArrayList<Integer>(a.length));
    }

    private static void printSubVectorsHelper(ArrayList<Integer> a, ArrayList<Integer> chosen) {
        if (a.isEmpty()) {
            System.out.println(chosen);
            return;
        }

        Integer first = a.get(0);
        a.remove(0);

        chosen.add(first);
        printSubVectorsHelper(a, chosen);

        chosen.remove(chosen.size() - 1);
        printSubVectorsHelper(a, chosen);
        a.add(0, first);
    }

    private static ArrayList<Integer> remove(ArrayList<Integer> a, int index) {
        ArrayList<Integer> copy = new ArrayList<>(a.size() - 1);

        for (int i = 0; i < a.size(); ++i) {
            if (i != index) copy.add(a.get(i));
        }
        return copy;
    }

    public static void main(String[] args) {
        printSubVectors(new Integer[] {1, 2, 3});
        //printSubVectors(new Integer[] {42, 23});
    }
}
