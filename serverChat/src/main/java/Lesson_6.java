import java.util.Arrays;

public class Lesson_6 {
    public static void main(String[] args) {


    }

    public static int[] arrayAfterFour(int[] arr) throws RuntimeException {
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == 4) {
                return Arrays.copyOfRange(arr, i + 1, arr.length);
            }
        }
        throw new IllegalArgumentException();
    }

    public static boolean arrayByOneAndFour(int[] arr) {
        boolean existOne = false;
        boolean existFour = false;
        for (int j : arr) {
            switch (j) {
                case 1:
                    existOne = true;
                    break;
                case 4:
                    existFour = true;
                    break;
                default:
                    return false;
            }
        }
        return existFour && existOne;
    }
}
