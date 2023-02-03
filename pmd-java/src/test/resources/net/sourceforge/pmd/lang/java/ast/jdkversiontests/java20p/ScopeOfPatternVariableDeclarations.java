/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/433">JEP 433: Pattern Matching for switch (Fourth Preview)</a>
 */
public class ScopeOfPatternVariableDeclarations {

    static void testSwitchBlock(Object obj) {
        switch (obj) {
            case Character c
                    when c.charValue() == 7:
                System.out.println("Ding!");
                break;
            default:
                break;
        }
    }

    static void testSwitchRule(Object o) {
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
                System.out.println("fall-through");
        }
    }


    public static void main(String[] args) {
        testSwitchBlock('\u0007');
        testSwitchRule('A');
        try {
            testSwitchRule(42); // throws
        } catch (IllegalStateException e) {
            System.out.println(e);
        }
        test2('\t');
    }
}
