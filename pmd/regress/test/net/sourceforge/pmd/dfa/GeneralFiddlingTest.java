package test.net.sourceforge.pmd.dfa;

import test.net.sourceforge.pmd.testframework.ParserTst;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.symboltable.SymbolFacade;
import net.sourceforge.pmd.dfa.DataFlowFacade;

import java.util.List;

public class GeneralFiddlingTest extends ParserTst {

    public void test1() throws Throwable {
        ASTCompilationUnit acu =  (ASTCompilationUnit)(getOrderedNodes(ASTCompilationUnit.class, TEST1).get(0));
        SymbolFacade sf = new SymbolFacade();
        sf.initializeWith(acu);
        DataFlowFacade df = new DataFlowFacade();
        df.initializeWith(acu);
/*
        ASTMethodDeclarator d = (ASTMethodDeclarator)nodes.get(0);
        ASTMethodDeclaration p = (ASTMethodDeclaration)d.jjtGetParent();
        System.out.println(p.getDataFlowNode());
*/
    }

    private static final String TEST1 =
        "class Foo {" + PMD.EOL +
        " void bar() {" + PMD.EOL +
        "  if (x == 0) {" + PMD.EOL +
        "   x++;" + PMD.EOL +
        "  }" + PMD.EOL +
        " }"  + PMD.EOL +
        "}";



}
