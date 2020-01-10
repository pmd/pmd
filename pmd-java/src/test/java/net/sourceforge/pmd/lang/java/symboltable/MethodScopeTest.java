/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class MethodScopeTest extends BaseNonParserTest {

    @Test
    public void testMethodParameterOccurrenceRecorded() {
        ASTCompilationUnit acu = parseCode(TEST1);
        Map<NameDeclaration, List<NameOccurrence>> m = acu.findDescendantsOfType(ASTMethodDeclaration.class).get(0)
                .getScope().getDeclarations();
        NameDeclaration vnd = m.keySet().iterator().next();
        assertEquals("bar", vnd.getImage());
        List<NameOccurrence> occs = m.get(vnd);
        NameOccurrence occ = occs.get(0);
        assertEquals(3, occ.getLocation().getBeginLine());
    }

    @Test
    public void testMethodName() {
        ASTCompilationUnit acu = parseCode(TEST1);
        ASTMethodDeclaration meth = acu.findDescendantsOfType(ASTMethodDeclaration.class).get(0);
        MethodScope ms = (MethodScope) meth.getScope();
        assertEquals(ms.getName(), "foo");
    }

    @Test
    public void testGenerics() {
        parseCode(TEST_GENERICS);
    }

    public static final String TEST1 = "public class Foo {" + PMD.EOL + " void foo(int bar) {" + PMD.EOL + "  bar = 2;"
            + PMD.EOL + " }" + PMD.EOL + "}";

    private static final String TEST_GENERICS = "public class Tree {" + PMD.EOL + "  private List<Object> subForest;"
            + PMD.EOL + "  public <B> Tree<B> fmap(final F<B> f) { return Tree.<B>foo(); }" + PMD.EOL
            + "  public List<Object> subForest() { return null; }" + PMD.EOL + "}" + PMD.EOL;
}
