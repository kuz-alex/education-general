import java.util.*;

public class Permutations {
    private static Set<String> generatePermutations(String str) {
        Set<String> result = new HashSet<>();

        if (str.length() == 0) {
            result.add("");
        } else {
            for (int i = 0; i < str.length(); ++i) {
                Character ch = str.charAt(i);
                String rest = str.substring(0, i) + str.substring(i + 1);
                Set<String> permutations = generatePermutations(rest);
                for (String permutation : permutations)
                    result.add(ch + permutation);
            }
            
        }

        return result;
    }

    public static void main(String[] args) {
        Set<String> res = generatePermutations("ABC");
        System.out.println(res);
        res = generatePermutations("AABB");
        System.out.println(res);
    }
}
