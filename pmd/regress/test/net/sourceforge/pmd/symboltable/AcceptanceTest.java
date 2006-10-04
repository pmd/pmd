/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

public class AcceptanceTest extends STBBaseTst {

/*
    public void testClashingSymbols() {
        parseCode(TEST1);
    }

    public void testInitializer() {
        parseCode(TEST_INITIALIZERS);
        ASTInitializer a = (ASTInitializer)(acu.findChildrenOfType(ASTInitializer.class)).get(0);
        assertFalse(a.isStatic());
        a = (ASTInitializer)(acu.findChildrenOfType(ASTInitializer.class)).get(1);
        assertTrue(a.isStatic());
    }

    public void testCatchBlocks() {
        parseCode(TEST_CATCH_BLOCKS);
        ASTCatchStatement c = (ASTCatchStatement)(acu.findChildrenOfType(ASTCatchStatement.class)).get(0);
        ASTBlock a = (ASTBlock)(c.findChildrenOfType(ASTBlock.class)).get(0);
        Scope s = a.getScope();
        Map vars = s.getParent().getVariableDeclarations();
        assertEquals(1, vars.size());
        VariableNameDeclaration v = (VariableNameDeclaration)vars.keySet().iterator().next();
        assertEquals("e", v.getImage());
        assertEquals(1, ((List)vars.get(v)).size());
    }

    public void testEq() {
        parseCode(TEST_EQ);
        ASTEqualityExpression e = (ASTEqualityExpression)(acu.findChildrenOfType(ASTEqualityExpression.class)).get(0);
        ASTMethodDeclaration method = (ASTMethodDeclaration)e.getFirstParentOfType(ASTMethodDeclaration.class);
        Scope s = method.getScope();
        Map m = s.getVariableDeclarations();
        for (Iterator i = m.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration vnd = (VariableNameDeclaration)i.next();
            SimpleNode node = vnd.getNode();
            //System.out.println();
        }
        //System.out.println(m.size());
    }
*/

    public void testFieldFinder() {
        System.out.println(TEST_FIELD);
        parseCode(TEST_FIELD);
        ASTVariableDeclaratorId declaration = (ASTVariableDeclaratorId)acu.findChildrenOfType(ASTVariableDeclaratorId.class).get(0);
        NameOccurrence no = (NameOccurrence)declaration.getUsages().iterator().next();
        SimpleNode location = no.getLocation();
        System.out.println("variable " + declaration.getImage() + " is used here: " + location.getImage());
    }

/*
    public void testDemo() {
        parseCode(TEST_DEMO);
        System.out.println(TEST_DEMO);
        ASTMethodDeclaration node = (ASTMethodDeclaration) acu.findChildrenOfType(ASTMethodDeclaration.class).get(0);
        Scope s = node.getScope();
        Map m = s.getVariableDeclarations();
        for (Iterator i = m.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration d = (VariableNameDeclaration) i.next();
            System.out.println("Variable: " + d.getImage());
            System.out.println("Type: " + d.getTypeImage());
        }
    }
*/
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


}
