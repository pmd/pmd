package test.net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.dfa.DataFlowFacade;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.symboltable.SymbolFacade;
import test.net.sourceforge.pmd.testframework.ParserTst;

import java.util.LinkedList;
import java.util.Iterator;

public class GeneralFiddlingTest extends ParserTst {

    public void test1() throws Throwable {
        ASTMethodDeclarator meth =  (ASTMethodDeclarator)(getOrderedNodes(ASTMethodDeclarator.class, TEST1).get(0));
        IDataFlowNode flow = meth.getDataFlowNode();
        for (Iterator i = flow.getFlow().iterator(); i.hasNext();) {
            IDataFlowNode o = (IDataFlowNode)i.next();
            System.out.println("Index:" + o.getIndex());
            if (o.getChildren().size() > 1) {
                System.out.println("Child 1 Index:" + ((IDataFlowNode)o.getChildren().get(1)).getIndex());
            }
            System.out.println(o);
            System.out.println("================");
        }
    }

    private static final String TEST1 =
        "class Foo {" + PMD.EOL +
        " void bar() {" + PMD.EOL +
        "  int x = 0;" + PMD.EOL +
        "  if (x == 0) {" + PMD.EOL +
        "   x++;" + PMD.EOL +
        "   x = 0;" + PMD.EOL +
        "  }" + PMD.EOL +
        " }"  + PMD.EOL +
        "}";



}
