package test.net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.dfa.DataFlowFacade;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.symboltable.SymbolFacade;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.LinkedList;
import java.util.Iterator;

public class GeneralFiddlingTest extends ParserTst {

    public void test1() throws Throwable {
        ASTMethodDeclaration meth =  (ASTMethodDeclaration)(getOrderedNodes(ASTMethodDeclaration.class, TEST1).get(0));
        IDataFlowNode flow = meth.getDataFlowNode();
        for (Iterator i = flow.getFlow().iterator(); i.hasNext();) {
            System.out.println(i.next());
        }


/*
        ASTMethodDeclarator d = (ASTMethodDeclarator)nodes.get(0);
        ASTMethodDeclaration p = (ASTMethodDeclaration)d.jjtGetParent();
        System.out.println(p.getDataFlowNode());
*/
    }

    private static final String TEST1 =
        "class Foo {" + PMD.EOL +
        " void bar() {" + PMD.EOL +
        "  while (x == 0) {" + PMD.EOL +
        "  int y;" + PMD.EOL +
        "  }" + PMD.EOL +
        " }"  + PMD.EOL +
        "}";



}
