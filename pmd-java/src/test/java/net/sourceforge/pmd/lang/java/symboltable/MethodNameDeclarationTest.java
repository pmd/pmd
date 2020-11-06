/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class MethodNameDeclarationTest extends BaseNonParserTest {

    @Test
    public void testEquality() {
        // Verify proper number of nodes are not equal
        ASTCompilationUnit acu = java5.parse(SIMILAR);
        ASTClassOrInterfaceDeclaration n = acu.findDescendantsOfType(ASTClassOrInterfaceDeclaration.class).get(0);
        Map<NameDeclaration, List<NameOccurrence>> m = ((ClassScope) n.getScope()).getDeclarations();
        Set<NameDeclaration> methodNameDeclarations = m.keySet();
        assertEquals("Wrong number of method name declarations", methodNameDeclarations.size(), 3);
    }

    private static final String SIMILAR = "public class Foo {" + PMD.EOL + " public void bar() {" + PMD.EOL
            + "  bar(x, y);" + PMD.EOL + " }" + PMD.EOL + " private void bar(int x, int y) {}" + PMD.EOL
            + " private void bar(int x, int... y) {}" + PMD.EOL + "}";
}
