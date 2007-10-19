/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTCatchStatement;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
public class AcceptanceTest extends STBBaseTst {

    @Ignore
    @Test
    public void testClashingSymbols() {
        parseCode(TEST1);
    }

    @Ignore
    @Test
    public void testInitializer() {
        parseCode(TEST_INITIALIZERS);
        ASTInitializer a = acu.findChildrenOfType(ASTInitializer.class).get(0);
        assertFalse(a.isStatic());
        a = acu.findChildrenOfType(ASTInitializer.class).get(1);
        assertTrue(a.isStatic());
    }

    @Ignore
    @Test
    public void testCatchBlocks() {
        parseCode(TEST_CATCH_BLOCKS);
        ASTCatchStatement c = acu.findChildrenOfType(ASTCatchStatement.class).get(0);
        ASTBlock a = c.findChildrenOfType(ASTBlock.class).get(0);
        Scope s = a.getScope();
        Map vars = s.getParent().getVariableDeclarations();
        assertEquals(1, vars.size());
        VariableNameDeclaration v = (VariableNameDeclaration)vars.keySet().iterator().next();
        assertEquals("e", v.getImage());
        assertEquals(1, ((List)vars.get(v)).size());
    }

    @Ignore
    @Test
    public void testEq() {
        parseCode(TEST_EQ);
        ASTEqualityExpression e = acu.findChildrenOfType(ASTEqualityExpression.class).get(0);
        ASTMethodDeclaration method = e.getFirstParentOfType(ASTMethodDeclaration.class);
        Scope s = method.getScope();
        Map m = s.getVariableDeclarations();
        for (Iterator i = m.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration vnd = (VariableNameDeclaration)i.next();
            SimpleNode node = vnd.getNode();
            //System.out.println();
        }
        //System.out.println(m.size());
    }

    @Test
    public void testFieldFinder() {
        //FIXME - Does this test do anything?
        //Not really, I think it's just a demo -- Tom

/*
        System.out.println(TEST_FIELD);
        parseCode(TEST_FIELD);

        List<ASTVariableDeclaratorId> variableDeclaratorIds = acu.findChildrenOfType(ASTVariableDeclaratorId.class);
        ASTVariableDeclaratorId declaration = null;
        for (Iterator iter = variableDeclaratorIds.iterator(); iter.hasNext();) {
            declaration = (ASTVariableDeclaratorId) iter.next();
            if ("b".equals(declaration.getImage()))
                break;
        }
        NameOccurrence no = declaration.getUsages().iterator().next();
        SimpleNode location = no.getLocation();
        System.out.println("variable " + declaration.getImage() + " is used here: " + location.getImage());
*/
    }

    @Ignore
    @Test
    public void testDemo() {
        parseCode(TEST_DEMO);
        System.out.println(TEST_DEMO);
        ASTMethodDeclaration node = acu.findChildrenOfType(ASTMethodDeclaration.class).get(0);
        Scope s = node.getScope();
        Map m = s.getVariableDeclarations();
        for (Iterator i = m.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration d = (VariableNameDeclaration) i.next();
            System.out.println("Variable: " + d.getImage());
            System.out.println("Type: " + d.getTypeImage());
        }
    }
/*
            List u = (List)m.get(d);
            System.out.println("Usages: " + u.size());
            NameOccurrence o = (NameOccurrence)u.get(0);
            int beginLine = o.getLocation().getBeginLine();
            System.out.println("Used in line " + beginLine);
*/

    private static final String TEST_DEMO =
            "public class Foo  {" + PMD.EOL +
            " void bar(ArrayList buz) { " + PMD.EOL +
            " } " + PMD.EOL +
            "}" + PMD.EOL;

    private static final String TEST_EQ =
            "public class Foo  {" + PMD.EOL +
            " boolean foo(String a, String b) { " + PMD.EOL +
            "  return a == b; " + PMD.EOL +
            " } " + PMD.EOL +
            "}" + PMD.EOL;

    private static final String TEST1 =
            "import java.io.*;" + PMD.EOL +
            "public class Foo  {" + PMD.EOL +
            " void buz( ) {" + PMD.EOL +
            "  Object o = new Serializable() { int x; };" + PMD.EOL +
            "  Object o1 = new Serializable() { int x; };" + PMD.EOL +
            " }" + PMD.EOL +
            "}" + PMD.EOL;

    private static final String TEST_INITIALIZERS =
            "public class Foo  {" + PMD.EOL +
            " {} " + PMD.EOL +
            " static {} " + PMD.EOL +
            "}" + PMD.EOL;

    private static final String TEST_CATCH_BLOCKS =
            "public class Foo  {" + PMD.EOL +
            " void foo() { " + PMD.EOL +
            "  try { " + PMD.EOL +
            "  } catch (Exception e) { " + PMD.EOL +
            "   e.printStackTrace(); " + PMD.EOL +
            "  } " + PMD.EOL +
            " } " + PMD.EOL +
            "}" + PMD.EOL;

    private static final String TEST_FIELD =
    "public class MyClass {" + PMD.EOL +
    " private int a; " + PMD.EOL +
    " boolean b = MyClass.ASCENDING; " + PMD.EOL +
    "}" + PMD.EOL;

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AcceptanceTest.class);
    }
}
