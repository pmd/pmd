package net.sourceforge.pmd.dfa;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.VariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.testframework.ParserTst;

import org.junit.Test;


public class GeneralFiddlingTest extends ParserTst {

    @Test
    public void test1() throws Throwable {
        ASTCompilationUnit acu = buildDFA(TEST1);
        ASTMethodDeclarator meth = acu.findDescendantsOfType(ASTMethodDeclarator.class).get(0);
        DataFlowNode n = meth.getDataFlowNode();
        List f = n.getFlow();
        for (Iterator i = f.iterator(); i.hasNext();) {
            DataFlowNode dfan = (DataFlowNode) i.next();
            System.out.println(dfan);
            List va = dfan.getVariableAccess();
            for (Iterator j = va.iterator(); j.hasNext();) {
                VariableAccess o = (VariableAccess) j.next();
                System.out.println(o);
            }
        }
    }

    private static final String TEST1 =
            "class Foo {" + PMD.EOL +
            " void bar() {" + PMD.EOL +
            "  int x = 2;" + PMD.EOL +
            "  foo(x);" + PMD.EOL +
            "  x = 3;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(GeneralFiddlingTest.class);
    }
}
