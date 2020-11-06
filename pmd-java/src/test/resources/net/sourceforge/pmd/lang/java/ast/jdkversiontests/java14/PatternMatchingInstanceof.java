/**
 * 
 * @see <a href="https://openjdk.java.net/jeps/305">JEP 305: Pattern Matching for instanceof (Preview)</a>
 */
public class PatternMatchingInstanceof {
    private String s = "other string";

    public void test() {
        Object obj = "abc";
        //obj = 1;
        if (obj instanceof String s) {
            System.out.println("a) obj == s: " + (obj == s)); // true
        } else {
            System.out.println("b) obj == s: " + (obj == s)); // false
        }

        if (!(obj instanceof String s)) {
            System.out.println("c) obj == s: " + (obj == s)); // false
        } else {
            System.out.println("d) obj == s: " + (obj == s)); // true
        }

        if (obj instanceof String s && s.length() > 2) {
            System.out.println("e) obj == s: " + (obj == s)); // true
        }
        if (obj instanceof String s || s.length() > 5) {
            System.out.println("f) obj == s: " + (obj == s)); // false
        }
    }

    public static void main(String[] args) {
        new PatternMatchingInstanceof().test();
    }
}