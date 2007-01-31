/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.symboltable.NameFinder;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.List;

public class NameOccurrencesTest extends STBBaseTst {

    public void testSuper() {
        parseCode(TEST1);
        List nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder((ASTPrimaryExpression) nodes.get(0));
        assertEquals("super", occs.getNames().get(0).getImage());
    }

    public void testThis() {
        parseCode(TEST2);
        List nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder((ASTPrimaryExpression) nodes.get(0));
        assertEquals("this", occs.getNames().get(0).getImage());
        assertEquals("x", occs.getNames().get(1).getImage());
    }

    public void testNameLinkage() {
        parseCode(TEST2);
        List nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder((ASTPrimaryExpression) nodes.get(0));
        NameOccurrence thisNameOccurrence = occs.getNames().get(0);
        assertEquals(thisNameOccurrence.getNameForWhichThisIsAQualifier(), occs.getNames().get(1));
    }

    public void testSimpleVariableOccurrence() {
        parseCode(TEST3);
        List nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder((ASTPrimaryExpression) nodes.get(0));
        assertEquals("x", occs.getNames().get(0).getImage());
        assertFalse(occs.getNames().get(0).isThisOrSuper());
        assertFalse(occs.getNames().get(0).isMethodOrConstructorInvocation());
        assertTrue(occs.getNames().get(0).isOnLeftHandSide());
    }

    public void testQualifiedOccurrence() {
        parseCode(TEST4);
        List nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder((ASTPrimaryExpression) nodes.get(0));
        assertEquals("b", occs.getNames().get(0).getImage());
        assertEquals("x", occs.getNames().get(1).getImage());
    }
    
    public void testIsSelfAssignment(){
        parseCode(TEST5);
        List nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder((ASTPrimaryExpression) nodes.get(2));
        assertTrue(occs.getNames().get(0).isSelfAssignment());

        parseCode(TEST6);
        nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        occs = new NameFinder((ASTPrimaryExpression) nodes.get(2));
        assertTrue(occs.getNames().get(0).isSelfAssignment());
    }

    public static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  super.x = 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  this.x = 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  x = 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " void foo() {" + PMD.EOL +
            "  b.x = 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static final String TEST5 =
        "public class Foo{" + PMD.EOL +
        "    private int counter;" + PMD.EOL +
        "    private Foo(){" + PMD.EOL +
        "        counter = 0;" + PMD.EOL +
        "    }" + PMD.EOL +
        "    private int foo(){" + PMD.EOL +
        "        if (++counter < 3) {" + PMD.EOL +
        "            return 0;" + PMD.EOL +
        "        }" + PMD.EOL +
        "        return 1;" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";
    
    public static final String TEST6 =
        "public class Foo{" + PMD.EOL +
        "    private int counter;" + PMD.EOL +
        "    private Foo(){" + PMD.EOL +
        "        counter = 0;" + PMD.EOL +
        "    }" + PMD.EOL +
        "    private int foo(){" + PMD.EOL +
        "        if (++this.counter < 3) {" + PMD.EOL +
        "            return 0;" + PMD.EOL +
        "        }" + PMD.EOL +
        "        return 1;" + PMD.EOL +
        "    }" + PMD.EOL +
        "}";
}
