package test.net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import test.net.sourceforge.pmd.testframework.ParserTst;

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
        "  for (int i=0; i<10; i++) {}" + PMD.EOL +
        " }"  + PMD.EOL +
        "}";



}
