import edu.princeton.cs.algs4.*;
import java.util.*;

class Permute {
    private static void permute(String s, String prefix) {
        if (s.length() == 0) {
            StdOut.println(prefix);
        } else {
            for (int i = 0; i < s.length(); ++i) {
                StringBuilder sb = new StringBuilder(s);
                sb.deleteCharAt(i);
                permute(sb.toString(), prefix + s.charAt(i));
            }
        }
    }

    private static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> list = new ArrayList<>();

        permuteBacktrack(list, new ArrayList<Integer>(), nums);
        return list;
    }

    private static void permuteBacktrack(
        List<List<Integer>> list,
        ArrayList<Integer> choice,
        int[] nums
    ) {
        // StdOut.printf("Nums: %s, choice: %s.\n", nums, choice);
        if (nums.length == choice.size()) {
            list.add(new ArrayList<Integer>(choice));
        } else {
            for (Integer i : nums) {
                if (choice.contains(i)) continue;
                choice.add(i);
                permuteBacktrack(list, choice, nums);
                choice.remove(choice.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        //permute("MARTY", "");
        StdOut.println(permute(new int[]{1, 2, 3}));
    }
}
