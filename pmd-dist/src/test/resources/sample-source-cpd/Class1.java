/**
 * Example for a test CPD run to detect duplicates.
 */
public class Class1 {

    public void duplicatedMethod() {
        int x = 1;
        int y = 2;
        int z = x * y;
        int a = x * x + y * y;
        System.out.println("x=" + x + ",y=" + y + ",z=" + z + ",a=" + a);
    }
}
