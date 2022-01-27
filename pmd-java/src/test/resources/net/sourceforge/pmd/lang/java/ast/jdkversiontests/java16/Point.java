/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.java.net/jeps/395">JEP 395: Records</a>
 */
public record Point(int x, int y) {

    public static void main(String[] args) {
        Point p = new Point(1, 2);
        System.out.println("p = " + p);
    }
}
