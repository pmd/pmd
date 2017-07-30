/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitorAdapter;

/**
 * @author Clément Fournier
 */
public class ApexMetricsVisitor extends ApexParserVisitorAdapter {

    @Override
    public Object visit(ASTUserClass node, Object data) {
        ((ApexProjectMirror) data).addClass(node.getQualifiedName());
        return super.visit(node, data);
    }


    @Override
    public Object visit(ASTMethod node, Object data) {
        ((ApexProjectMirror) data).addOperation(node.getQualifiedName(), node.getSignature());
        return data;
    }

}
