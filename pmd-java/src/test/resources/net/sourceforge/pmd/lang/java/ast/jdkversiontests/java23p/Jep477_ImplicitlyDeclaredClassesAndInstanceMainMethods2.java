/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// explicit imports are possible as well (although not really needed in this example)
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @see <a href="https://openjdk.org/jeps/445">JEP 445: Unnamed Classes and Instance Main Methods (Preview)</a> (Java 21)
 * @see <a href="https://openjdk.org/jeps/463">JEP 463: Implicitly Declared Classes and Instance Main Methods (Second Preview)</a> (Java 22)
 * @see <a href="https://openjdk.org/jeps/477">JEP 477: Implicitly Declared Classes and Instance Main Methods (Third Preview)</a> (Java 23)
 */

// Top-level members are interpreted as members of the implicit class (methods and fields)
String greeting() { return "Hello, World! (method)"; }
String greetingText = "Hello, World! (text)";
String greetingText2 = Arrays.asList("Hello", "World!", "(with imports)").stream().collect(Collectors.joining(", "));

void main() {
    System.out.println(greeting());
    println(greetingText);
    println(greetingText2);
}
