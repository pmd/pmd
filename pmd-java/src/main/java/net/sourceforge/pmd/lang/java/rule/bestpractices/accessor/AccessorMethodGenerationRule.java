/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.accessor;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.reporting.RuleContext;

public class AccessorMethodGenerationRule extends AbstractJavaRulechainRule {

    private final Set<JavaNode> reportedNodes = new HashSet<>();

    public AccessorMethodGenerationRule() {
        super(ASTFieldAccess.class, ASTVariableAccess.class, ASTMethodCall.class);
    }

    @Override
    public void end(RuleContext ctx) {
        super.end(ctx);
        reportedNodes.clear();
    }

    @Override
    public Object visit(ASTFieldAccess node, Object data) {
        JFieldSymbol sym = node.getReferencedSym();
        if (sym != null && sym.getConstValue() == null) {
            checkMemberAccess((RuleContext) data, node, sym);
        }
        return null;
    }

    @Override
    public Object visit(ASTVariableAccess node, Object data) {
        visit(node, (RuleContext) data, node.getReferencedSym());
        return null;
    }

    private void visit(ASTVariableAccess node, RuleContext data, JVariableSymbol sym) {
        if (sym instanceof JFieldSymbol && ((JFieldSymbol) sym).getConstValue() == null) {
            checkMemberAccess(data, node, (JFieldSymbol) sym);
        }
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        checkMemberAccess((RuleContext) data, node, node.getMethodType().getSymbol());
        return null;
    }

    private void checkMemberAccess(RuleContext data, ASTExpression node, JAccessibleElementSymbol symbol) {
        AccessorHelper.checkMemberAccess(data, node, symbol, reportedNodes);
    }

}
