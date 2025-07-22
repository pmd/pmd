/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// explicit imports are possible as well (although not really needed in this example)
import java.util.Arrays;
import java.util.stream.Collectors;

import static java.lang.IO.println;

/**
 * @see <a href="https://openjdk.org/jeps/512">JEP 512: Compact Source Files and Instance Main Methods</a> (Java 25)
 */

// Top-level members are interpreted as members of the implicit class (methods and fields)
String greetingField = "Hello, World! (Field)";
String greeting() {
    return "Hello, World! (Method)";
}
String greetingText2 = Arrays.asList("Hello", "World!", "(with imports)").stream().collect(Collectors.joining(", "));

void main() {
    System.out.println(greetingField);
    IO.println(greeting()); // java.lang.IO.println via qualifier
    println(greetingText2); // java.lang.IO.println via static import

    // java.lang.IO.readln
    String name = IO.readln("Please enter your name: ");
    IO.print("Pleased to meet you, ");
    println(name);

    // java.util.List is automatically available via implicit "import module java.base"
    var authors = List.of("James", "Bill", "Guy", "Alex", "Dan", "Gavin");
    for (var authorName : authors) {
        println(authorName + ": " + authorName.length());
    }
}
