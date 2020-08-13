/**
 * @see <a href="https://openjdk.java.net/jeps/384">JEP 384: Records (Second Preview)</a>
 */
public record Point(int x, int y) {

    public static void main(String[] args) {
        Point p = new Point(1, 2);
        System.out.println("p = " + p);
    }
}
