/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/427">JEP 427: Pattern Matching for switch (Third Preview)</a>
 */
public class RefiningPatternsInSwitch {

    static class Shape {}
    static class Rectangle extends Shape {}
    static class Triangle  extends Shape {
        private int area;
        Triangle(int area) {
            this.area = area;
        }
        int calculateArea() { return area; }
    }

    static void testTriangle(Shape s) {
        switch (s) {
            case null:
                break;
            case Triangle t:
                if (t.calculateArea() > 100) {
                    System.out.println("Large triangle");
                    break;
                }
            default:
                System.out.println("A shape, possibly a small triangle");
        }
    }
    
    static void testTriangleRefined(Shape s) {
        switch (s) {
            case Triangle t when t.calculateArea() > 100 ->
                System.out.println("Large triangle");
            default ->
                System.out.println("A shape, possibly a small triangle");
        }
    }

    static void testTriangleRefined2(Shape s) {
        switch (s) {
            case Triangle t  when t.calculateArea() > 100 ->
                System.out.println("Large triangle");
            case Triangle t ->
                System.out.println("Small triangle");
            default ->
                System.out.println("Non-triangle");
        }
    }

    public static void main(String[] args) {
        Triangle large = new Triangle(200);
        Triangle small = new Triangle(10);
        Rectangle rect = new Rectangle();

        testTriangle(large);
        testTriangle(small);
        testTriangle(rect);

        testTriangleRefined(large);
        testTriangleRefined(small);
        testTriangleRefined(rect);

        testTriangleRefined2(large);
        testTriangleRefined2(small);
        testTriangleRefined2(rect);
    }
}
