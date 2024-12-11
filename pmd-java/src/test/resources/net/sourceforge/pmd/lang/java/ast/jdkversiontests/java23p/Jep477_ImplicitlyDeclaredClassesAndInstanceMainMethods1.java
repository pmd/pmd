/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */



/**
 * @see <a href="https://openjdk.org/jeps/445">JEP 445: Unnamed Classes and Instance Main Methods (Preview)</a> (Java 21)
 * @see <a href="https://openjdk.org/jeps/463">JEP 463: Implicitly Declared Classes and Instance Main Methods (Second Preview)</a> (Java 22)
 * @see <a href="https://openjdk.org/jeps/477">JEP 477: Implicitly Declared Classes and Instance Main Methods (Third Preview)</a> (Java 23)
 */

void main() {
    System.out.println("Hello World");
    println("Hello, World!"); // since JEP477, implicitly imports "import static java.io.IO.*"

    // this is java.io.IO.readln ...
    String name = readln("Please enter your name: ");
    print("Pleased to meet you, ");
    println(name);

    // this is java.util.List - by implicitly "import module java.base"
    var authors = List.of("James", "Bill", "Guy", "Alex", "Dan", "Gavin");
    for (var authorName : authors) {
        println(authorName + ": " + authorName.length());
    }
}
