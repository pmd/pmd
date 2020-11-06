/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.symboltable.BaseNonParserTest;

public class GeneralFiddlingTest extends BaseNonParserTest {

    /**
     * Unit test for https://sourceforge.net/p/pmd/bugs/1325/
     */
    @Test
    public void innerClassShouldWork() {
        ASTCompilationUnit acu = java.parse(
                "class Foo {" + "    void bar() {" + "        class X {}" + "        int i;" + "    }" + "}");
        assertNotNull(acu);
    }

    @Test
    public void test1() {
        ASTCompilationUnit acu = java.parse(TEST1);
        ASTMethodDeclarator meth = acu.findDescendantsOfType(ASTMethodDeclarator.class).get(0);
        DataFlowNode n = meth.getDataFlowNode();
        List<DataFlowNode> f = n.getFlow();

        assertEquals(6, f.size());
        assertEquals("Undefinition(x)", String.valueOf(f.get(0).getVariableAccess().get(0)));
        assertEquals(0, f.get(1).getVariableAccess().size());
        assertEquals("Definition(x)", String.valueOf(f.get(2).getVariableAccess().get(0)));
        assertEquals("Reference(x)", String.valueOf(f.get(3).getVariableAccess().get(0)));
        assertEquals("Definition(x)", String.valueOf(f.get(4).getVariableAccess().get(0)));
        assertEquals("Undefinition(x)", String.valueOf(f.get(5).getVariableAccess().get(0)));

        // for (DataFlowNode dfan : f) {
        // System.out.println("Flow starting on line " + dfan.getLine());
        // List<VariableAccess> va = dfan.getVariableAccess();
        // for (VariableAccess o : va) {
        // System.out.println(" variable: " + o);
        // }
        // }
    }

    private static final String TEST1 = "class Foo {" + PMD.EOL + " void bar() {" + PMD.EOL + "  int x = 2;" + PMD.EOL
            + "  foo(x);" + PMD.EOL + "  x = 3;" + PMD.EOL + " }" + PMD.EOL + "}";
}
