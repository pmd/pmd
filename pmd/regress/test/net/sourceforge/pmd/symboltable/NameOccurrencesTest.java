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
        assertEquals("super", ((NameOccurrence) occs.getNames().get(0)).getImage());
    }

    public void testThis() {
        parseCode(TEST2);
        List nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder((ASTPrimaryExpression) nodes.get(0));
        assertEquals("this", ((NameOccurrence) occs.getNames().get(0)).getImage());
        assertEquals("x", ((NameOccurrence) occs.getNames().get(1)).getImage());
    }

    public void testNameLinkage() {
        parseCode(TEST2);
        List nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder((ASTPrimaryExpression) nodes.get(0));
        NameOccurrence thisNameOccurrence = (NameOccurrence) occs.getNames().get(0);
        assertEquals(thisNameOccurrence.getNameForWhichThisIsAQualifier(), occs.getNames().get(1));
    }

    public void testSimpleVariableOccurrence() {
        parseCode(TEST3);
        List nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder((ASTPrimaryExpression) nodes.get(0));
        assertEquals("x", ((NameOccurrence) occs.getNames().get(0)).getImage());
        assertFalse(((NameOccurrence) occs.getNames().get(0)).isThisOrSuper());
        assertFalse(((NameOccurrence) occs.getNames().get(0)).isMethodOrConstructorInvocation());
        assertTrue(((NameOccurrence) occs.getNames().get(0)).isOnLeftHandSide());
    }

    public void testQualifiedOccurrence() {
        parseCode(TEST4);
        List nodes = acu.findChildrenOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder((ASTPrimaryExpression) nodes.get(0));
        assertEquals("b", ((NameOccurrence) occs.getNames().get(0)).getImage());
        assertEquals("x", ((NameOccurrence) occs.getNames().get(1)).getImage());
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
}
