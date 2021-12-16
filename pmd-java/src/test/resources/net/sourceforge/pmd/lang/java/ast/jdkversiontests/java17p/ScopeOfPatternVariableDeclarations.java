/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.java.net/jeps/406">JEP 406: Pattern Matching for switch (Preview)</a>
 */
public class ScopeOfPatternVariableDeclarations {


    static void test(Object o) {
        switch (o) {
            case Character c -> {
                if (c.charValue() == 7) {
                    System.out.println("Ding!");
                }
                System.out.println("Character");
            }
            case Integer i ->
                throw new IllegalStateException("Invalid Integer argument of value " + i.intValue());
            default -> {
                break;
            }
        }
    }


    static void test2(Object o) {
        switch (o) {
            case Character c:
                if (c.charValue() == 7) {
                    System.out.print("Ding ");
                }
                if (c.charValue() == 9) {
                    System.out.print("Tab ");
                }
                System.out.println("character");
            default:
                System.out.println();
        }
    }


    public static void main(String[] args) {
        test('A');
        test2('\t');
    }
}
