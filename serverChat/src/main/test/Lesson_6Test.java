import org.junit.Assert;
import org.junit.Test;

public class Lesson_6Test {

    @Test
    public void toearrayAfterFour() {
        int[] arr = Lesson_6.arrayAfterFour(new int[]{1, 4, 3, 6, 4, 9, 4, 56, 7});
        int[] referenceArr = new int[]{56, 7};
        Assert.assertArrayEquals(arr, referenceArr);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceptionarrayAfterFour() throws RuntimeException {
        int[] arr = Lesson_6.arrayAfterFour(new int[]{1, 2, 3, 6, 58, 9, 7, 56, 7});

    }

    @Test
    public void lastarrayAfterFour() {
        int[] arr = Lesson_6.arrayAfterFour(new int[]{1, 2, 3, 6, 58, 9, 7, 56, 4});
        int[] referenceArr = new int[]{};
        Assert.assertArrayEquals(arr, referenceArr);
    }

    @Test
    public void firstarrayAfterFour() {
        int[] arr = Lesson_6.arrayAfterFour(new int[]{4, 2, 3, 6, 58, 9, 7, 56, 7});
        int[] referenceArr = new int[]{2, 3, 6, 58, 9, 7, 56, 7};
        Assert.assertArrayEquals(arr, referenceArr);
    }
    @Test
    public void truearrayByOneAndFour(){
        Assert.assertTrue(Lesson_6.arrayByOneAndFour(new int[]{1, 4, 1, 4, 1, 4, 1, 4, 1}));
    }
    @Test
    public void falsearrayByOneAndFour(){
        Assert.assertFalse(Lesson_6.arrayByOneAndFour(new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1}));
    }
    @Test
    public void false2arrayByOneAndFour(){
        Assert.assertFalse(Lesson_6.arrayByOneAndFour(new int[]{1, 4, 5, 1, 1, 1, 1, 1, 1}));
    }
    @Test
    public void false3arrayByOneAndFour(){
        Assert.assertFalse(Lesson_6.arrayByOneAndFour(new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4}));
    }

}
