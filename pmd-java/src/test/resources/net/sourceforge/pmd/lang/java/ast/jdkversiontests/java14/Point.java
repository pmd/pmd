/**
 * @see <a href="https://openjdk.java.net/jeps/359">JEP 359: Records (Preview)</a>
 */
public record Point(int x, int y) {

    public static void main(String[] args) {
        Point p = new Point(1, 2);
        System.out.println("p = " + p);
    }
}
