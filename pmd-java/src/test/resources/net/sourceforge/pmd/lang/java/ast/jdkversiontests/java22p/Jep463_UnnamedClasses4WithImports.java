/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @see <a href="https://openjdk.org/jeps/445">JEP 445: Unnamed Classes and Instance Main Methods (Preview)</a> (Java 21)
 * @see <a href="https://openjdk.org/jeps/463">JEP 463: Implicitly Declared Classes and Instance Main Methods (Second Preview)</a> (Java 22)
 */

String greeting = Arrays.asList("Hello", "World!").stream().collect(Collectors.joining(", "));

void main() {
    System.out.println(greeting);
}
