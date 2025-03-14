import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.MODULE;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 
 * @see <a href="https://openjdk.java.net/jeps/394">JEP 394: Pattern Matching for instanceof</a>
 */
public class PatternMatchingInstanceof {
    private String s = "other string";

    public void test() {
        Object obj = "abc";
        //obj = 1;
        if (obj instanceof String s) {
            System.out.println("a) obj == s: " + (obj == s)); // true
            s = "other value"; // not a compile error - s is only effectively final
            System.out.println("changed s to " + s + ": obj == s: " + (obj == s));
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

        // With Java16 there can be final and annotations
        if (obj instanceof final String s) {
            System.out.println("g) obj == s: " + (obj == s)); // true
            //s = "another value"; // compile error: error: cannot assign a value to final variable s
        } else {
            System.out.println("h) obj == s: " + (obj == s)); // false
        }
        if (obj instanceof @Deprecated String s) {
            System.out.println("i) obj == s: " + (obj == s)); // true
        } else {
            System.out.println("j) obj == s: " + (obj == s)); // false
        }
        if (obj instanceof final @Deprecated String s) {
            System.out.println("k) obj == s: " + (obj == s)); // true
            //s = "another value"; // compile error: error: cannot assign a value to final variable s
        } else {
            System.out.println("l) obj == s: " + (obj == s)); // false
        }
    }

    public static void main(String[] args) {
        new PatternMatchingInstanceof().test();
    }

    // InstanceofExpression can be annotated
    class Foo {
        {
            Object f = null;
            Object o = f instanceof @Nullable Foo;
        }
    }

    @Target(value=ElementType.TYPE_USE)
    @interface Nullable { }
}