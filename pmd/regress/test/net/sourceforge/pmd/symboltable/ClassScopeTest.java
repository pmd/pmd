/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.symboltable;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class ClassScopeTest extends STBBaseTst {

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

    private static final String CLASS_NAME =
    "public class Foo {}";

    private static final String ENUM_SCOPE =
    "public enum Foo {" + PMD.EOL +
    " HEAP(\"foo\");" + PMD.EOL +
    " private final String fuz;" + PMD.EOL +
    " public String getFuz() {" + PMD.EOL +
    "  return fuz;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
