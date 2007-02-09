package test.net.sourceforge.pmd.dfa;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.dfa.DataFlowNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.Iterator;
import java.util.List;

public class GeneralFiddlingTest extends ParserTst {

    @Test
    public void test1() throws Throwable {
        ASTCompilationUnit acu = buildDFA(TEST1);
        ASTMethodDeclarator meth = acu.findChildrenOfType(ASTMethodDeclarator.class).get(0);
        IDataFlowNode n = meth.getDataFlowNode();
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
