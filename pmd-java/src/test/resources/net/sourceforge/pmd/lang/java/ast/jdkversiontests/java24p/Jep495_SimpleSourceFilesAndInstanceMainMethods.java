/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// explicit imports are possible as well (although not really needed in this example)
import java.util.Arrays;
import java.util.stream.Collectors;
import static java.io.IO.*; // support for the implicit import in PMD and Java 25 has been removed with JEP 512

/**
 * @see <a href="https://openjdk.org/jeps/495">JEP 495: Simple Source Files and Instance Main Methods (Fourth Preview)</a> (Java 24)
 */

// Top-level members are interpreted as members of the implicit class (methods and fields)
String greetingField = "Hello, World! (Field)";
String greeting() {
    return "Hello, World! (Method)";
}
String greetingText2 = Arrays.asList("Hello", "World!", "(with imports)").stream().collect(Collectors.joining(", "));

void main() {
    System.out.println(greetingField);
    println(greeting()); //java.io.IO.println
    println(greetingText2);

    // java.io.IO.readln
    String name = readln("Please enter your name: ");
    print("Pleased to meet you, ");
    println(name);

    // java.util.List is automatically available via implicit "import module java.base"
    var authors = List.of("James", "Bill", "Guy", "Alex", "Dan", "Gavin");
    for (var authorName : authors) {
        println(authorName + ": " + authorName.length());
    }
}
