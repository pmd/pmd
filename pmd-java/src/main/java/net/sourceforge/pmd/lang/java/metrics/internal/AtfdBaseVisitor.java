/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThisExpression;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;


/**
 * Computes Atfd.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class AtfdBaseVisitor extends JavaVisitorBase<MutableInt, Void> {

    @Override
    public Void visit(ASTMethodCall node, MutableInt data) {
        if (isForeignMethod(node)) {
            data.increment();
        }
        return visitChildren(node, data);
    }

    @Override
    public Void visit(ASTFieldAccess node, MutableInt data) {
        if (isForeignField(node)) {
            data.increment();
        }
        return visitChildren(node, data);
    }

    private boolean isForeignField(ASTFieldAccess node) {
        JFieldSymbol sym = node.getReferencedSym();
        if (sym == null || sym.isStatic()) {
            return false;
        }
        ASTExpression qualifier = node.getQualifier();
        return !(qualifier instanceof ASTThisExpression
            || qualifier instanceof ASTSuperExpression
            || sym.getEnclosingClass().equals(node.getEnclosingType().getSymbol())
            );
    }

    private boolean isForeignMethod(ASTMethodCall node) {
        return JavaRuleUtil.isGetterOrSetterCall(node)
                  && node.getQualifier() != null
                  && !(node.getQualifier() instanceof ASTThisExpression);
    }

}
