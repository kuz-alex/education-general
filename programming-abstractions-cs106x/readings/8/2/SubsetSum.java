import java.util.*;

public class SubsetSum {
    public static boolean subsetSumExist(int[] list, int target) {
        return subsetSumExistHelper(list, 0, target);
    }

    private static boolean subsetSumExistHelper(int[] list, int index, int target) {
        if (list.length == index) {
            return target == 0;
        }

        int element = list[index];
        return subsetSumExistHelper(list, index + 1, target)
            || subsetSumExistHelper(list, index + 1, target - element);
    }

    public static void main(String[] args) {
        System.out.println(subsetSumExist(new int[] {-2, 1, 3, 8}, 7));
        System.out.println(subsetSumExist(new int[] {-2, 1, 3, 8}, 5));
    }
}

