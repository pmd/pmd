/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;

public class NameOccurrencesTest extends BaseNonParserTest {

    @Test
    public void testSuper() {
        ASTCompilationUnit acu = parseCode(TEST1);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(0));
        assertEquals("super", occs.getNames().get(0).getImage());
    }

    @Test
    public void testThis() {
        ASTCompilationUnit acu = parseCode(TEST2);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(0));
        assertEquals("this", occs.getNames().get(0).getImage());
        assertEquals("x", occs.getNames().get(1).getImage());
    }

    @Test
    public void testNameLinkage() {
        ASTCompilationUnit acu = parseCode(TEST2);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(0));
        JavaNameOccurrence thisNameOccurrence = occs.getNames().get(0);
        assertEquals(thisNameOccurrence.getNameForWhichThisIsAQualifier(), occs.getNames().get(1));
    }

    @Test
    public void testSimpleVariableOccurrence() {
        ASTCompilationUnit acu = parseCode(TEST3);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(0));
        assertEquals("x", occs.getNames().get(0).getImage());
        assertFalse(occs.getNames().get(0).isThisOrSuper());
        assertFalse(occs.getNames().get(0).isMethodOrConstructorInvocation());
        assertTrue(occs.getNames().get(0).isOnLeftHandSide());
    }

    @Test
    public void testQualifiedOccurrence() {
        ASTCompilationUnit acu = parseCode(TEST4);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(0));
        assertEquals("b", occs.getNames().get(0).getImage());
        assertEquals("x", occs.getNames().get(1).getImage());
    }

    @Test
    public void testIsSelfAssignment() {
        ASTCompilationUnit acu = parseCode(TEST5);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        NameFinder occs = new NameFinder(nodes.get(2));
        assertTrue(occs.getNames().get(0).isSelfAssignment());

        acu = parseCode(TEST6);
        nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);
        occs = new NameFinder(nodes.get(2));
        assertTrue(occs.getNames().get(0).isSelfAssignment());
    }

    @Test
    public void testEnumStaticUsage() {
        ASTCompilationUnit acu = parseCode(TEST_ENUM);
        List<ASTPrimaryExpression> nodes = acu.findDescendantsOfType(ASTPrimaryExpression.class);

        NameFinder occs = new NameFinder(nodes.get(4));
        List<JavaNameOccurrence> names = occs.getNames();
        assertEquals(3, names.size());
        assertEquals("myEnum", names.get(0).getImage());
        assertFalse(names.get(0).isMethodOrConstructorInvocation());
        assertEquals("desc", names.get(1).getImage());
        assertFalse(names.get(1).isMethodOrConstructorInvocation());
        assertEquals("equals", names.get(2).getImage());
        assertTrue(names.get(2).isMethodOrConstructorInvocation());
    }

    public static final String TEST1 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  super.x = 2;"
            + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST2 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  this.x = 2;"
            + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST3 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  x = 2;" + PMD.EOL
            + " }" + PMD.EOL + "}";

    public static final String TEST4 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  b.x = 2;"
            + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST5 = "public class Foo{" + PMD.EOL + "    private int counter;" + PMD.EOL
            + "    private Foo(){" + PMD.EOL + "        counter = 0;" + PMD.EOL + "    }" + PMD.EOL
            + "    private int foo(){" + PMD.EOL + "        if (++counter < 3) {" + PMD.EOL + "            return 0;"
            + PMD.EOL + "        }" + PMD.EOL + "        return 1;" + PMD.EOL + "    }" + PMD.EOL + "}";

    public static final String TEST6 = "public class Foo{" + PMD.EOL + "    private int counter;" + PMD.EOL
            + "    private Foo(){" + PMD.EOL + "        counter = 0;" + PMD.EOL + "    }" + PMD.EOL
            + "    private int foo(){" + PMD.EOL + "        if (++this.counter < 3) {" + PMD.EOL
            + "            return 0;" + PMD.EOL + "        }" + PMD.EOL + "        return 1;" + PMD.EOL + "    }"
            + PMD.EOL + "}";

    public static final String TEST_ENUM = "public enum MyEnum {" + PMD.EOL + "  A(\"a\");" + PMD.EOL
            + "  private final String desc;" + PMD.EOL + "  private MyEnum(String desc) {" + PMD.EOL
            + "    this.desc = desc;" + PMD.EOL + "  }" + PMD.EOL + "  public static MyEnum byDesc(String desc) {"
            + PMD.EOL + "    for (MyEnum myEnum : value()) {" + PMD.EOL
            + "      if (myEnum.desc.equals(desc)) return myEnum;" + PMD.EOL + "    }" + PMD.EOL + "    return null;"
            + PMD.EOL + "  }" + PMD.EOL + " }";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(NameOccurrencesTest.class);
    }
}
