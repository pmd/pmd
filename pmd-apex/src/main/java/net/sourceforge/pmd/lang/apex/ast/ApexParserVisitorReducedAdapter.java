/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

/**
 * @author Clément Fournier
 */
public class ApexParserVisitorReducedAdapter extends ApexParserVisitorAdapter {


    @Override
    public final Object visit(ASTUserInterface node, Object data) {
        return visit((ASTUserClassOrInterface<?>) node, data);
    }


    @Override
    public final Object visit(ASTUserClass node, Object data) {
        return visit((ASTUserClassOrInterface<?>) node, data);
    }


    public Object visit(ASTUserClassOrInterface<?> node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

}
