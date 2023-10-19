/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */



/**
 * @see <a href="https://openjdk.org/jeps/445">JEP 445: Unnamed Classes and Instance Main Methods (Preview)</a>
 */

String greeting() { return "Hello, World!"; }

void main() {
    System.out.println(greeting());
}
