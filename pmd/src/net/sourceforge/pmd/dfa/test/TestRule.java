/*
 * Created on 18.08.2004
 */
package net.sourceforge.pmd.dfa.test;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.dfa.IDataFlowNode;

import java.util.List;

/**
 * @author raik
 *         <p/>
 *         Test rule to verify special data flows.
 */
public class TestRule extends AbstractRule {

    FlowTest ft = new FlowTest();

    public Object visit(ASTMethodDeclarator node, Object data) {
        super.visit(node, data);

        String methodName = node.getImage();

        IDataFlowNode inode = node.getDataFlowNode();
        if (inode == null) return data;

        List flow = inode.getFlow();
        if (flow == null) return data;


        ft.run(methodName, flow);

        return data;
    }
}
