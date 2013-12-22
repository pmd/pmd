package net.sourceforge.pmd.symboltable;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

import org.junit.Test;

public class MethodNameDeclarationTest extends STBBaseTst {

    @Test
    public void testEquality() {
    	// Verify proper number of nodes are not equal
        parseCode15(SIMILAR);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();
        Set<NameDeclaration> methodNameDeclarations = m.keySet();
        assertEquals("Wrong number of method name declarations", methodNameDeclarations.size(), 3);
    }

    private static final String SIMILAR =
            "public class Foo {" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  bar(x, y);" + PMD.EOL +
            " }" + PMD.EOL +
            " private void bar(int x, int y) {}" + PMD.EOL +
            " private void bar(int x, int... y) {}" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MethodNameDeclarationTest.class);
    }
}
