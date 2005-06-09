/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ClassScopeTest extends STBBaseTst {

    public void testEnumsClassScope() {
        parseCode15(ENUM_SCOPE);
    }

    // FIXME - these will break when this goes from Anonymous$1 to Foo$1
    public void testAnonymousInnerClassName() {
        ClassScope s = new ClassScope();
        assertEquals("Anonymous$1", s.getClassName());
        s = new ClassScope();
        assertEquals("Anonymous$2", s.getClassName());
    }

    public void testContains() {
        ClassScope s = new ClassScope("Foo");
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("bar");
        s.addDeclaration(new VariableNameDeclaration(node));
        assertTrue(s.getVariableDeclarations().keySet().iterator().hasNext());
    }

    public void testCantContainsSuperToString() {
        ClassScope s = new ClassScope("Foo");
        SimpleNode node = new SimpleNode(1);
        node.setImage("super.toString");
        assertTrue(!s.contains(new NameOccurrence(node, node.getImage())));
    }

    public void testContainsStaticVariablePrefixedWithClassName() {
        ClassScope s = new ClassScope("Foo");
        ASTVariableDeclaratorId node = new ASTVariableDeclaratorId(1);
        node.setImage("X");
        s.addDeclaration(new VariableNameDeclaration(node));

        SimpleNode node2 = new SimpleNode(2);
        node2.setImage("Foo.X");
        assertTrue(s.contains(new NameOccurrence(node2, node2.getImage())));
    }

    public void testClassName() {
        parseCode(CLASS_NAME);
        ASTClassOrInterfaceDeclaration n = (ASTClassOrInterfaceDeclaration)acu.findChildrenOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        assertEquals("Foo", n.getScope().getEnclosingClassScope().getClassName());
    }

    public void testMethodDeclarationRecorded() {
        parseCode(METHOD_DECLARATIONS_RECORDED);
        ASTClassOrInterfaceDeclaration n = (ASTClassOrInterfaceDeclaration)acu.findChildrenOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        ClassScope s = (ClassScope)n.getScope();
        Map m = s.getMethodDeclarations();
        assertEquals(1, m.size());
        MethodNameDeclaration mnd = (MethodNameDeclaration)m.keySet().iterator().next();
        assertEquals("bar", mnd.getImage());
        ASTMethodDeclaration node = (ASTMethodDeclaration)mnd.getNode().jjtGetParent();
        assertTrue(node.isPrivate());
    }

    public void testTwoMethodsSameNameDiffArgs() {
        // TODO this won't work with String and java.lang.String
        parseCode(METHODS_WITH_DIFF_ARG);
        ASTClassOrInterfaceDeclaration n = (ASTClassOrInterfaceDeclaration)acu.findChildrenOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope)n.getScope()).getMethodDeclarations();
        assertEquals(2, m.size());
        Iterator i = m.keySet().iterator();
        MethodNameDeclaration mnd = (MethodNameDeclaration)i.next();
        assertEquals("bar", mnd.getImage());
        assertEquals("bar", ((MethodNameDeclaration)i.next()).getImage());
    }

    public void testMethodUsageSeen() {
        parseCode(METHOD_USAGE_SEEN);
        ASTClassOrInterfaceDeclaration n = (ASTClassOrInterfaceDeclaration)acu.findChildrenOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map m = ((ClassScope)n.getScope()).getMethodDeclarations();
        Iterator i = m.keySet().iterator();
        MethodNameDeclaration mnd = (MethodNameDeclaration)i.next();
        if (!mnd.getImage().equals("bar")) {
            mnd = (MethodNameDeclaration)i.next();
        }
        List usages = (List)m.get(mnd);
        // FIXME
        //assertEquals(1, usages.size());
        //assertEquals("bar", ((MethodNameDeclaration)m.keySet().iterator().next()).getImage());
    }

    private static final String CLASS_NAME =
    "public class Foo {}";

    private static final String METHOD_DECLARATIONS_RECORDED =
    "public class Foo {" + PMD.EOL +
    " private void bar() {}" + PMD.EOL +
    "}";

    private static final String METHODS_WITH_DIFF_ARG =
    "public class Foo {" + PMD.EOL +
    " private void bar(String x) {}" + PMD.EOL +
    " private void bar() {}" + PMD.EOL +
    "}";

    private static final String METHOD_USAGE_SEEN =
    "public class Foo {" + PMD.EOL +
    " private void bar() {}" + PMD.EOL +
    " private void buz(String x) {" + PMD.EOL +
    "  bar();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String ENUM_SCOPE =
    "public enum Foo {" + PMD.EOL +
    " HEAP(\"foo\");" + PMD.EOL +
    " private final String fuz;" + PMD.EOL +
    " public String getFuz() {" + PMD.EOL +
    "  return fuz;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
