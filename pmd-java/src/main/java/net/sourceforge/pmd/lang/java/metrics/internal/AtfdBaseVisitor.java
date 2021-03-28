/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
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
        JFieldSymbol sym = node.getReferencedSym();
        if (sym != null && !sym.getEnclosingClass().equals(node.getEnclosingType().getSymbol())) {
            data.increment();
        }
        return visitChildren(node, data);
    }

    private boolean isForeignMethod(ASTMethodCall node) {
        return JavaRuleUtil.isGetterOrSetterCall(node) // getter or setter
            && node.getQualifier() != null             // not called on this
            && !(node.getQualifier() instanceof ASTThisExpression);
    }

}
