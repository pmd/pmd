/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.rule.AbstractRule;

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
        JVariableSymbol sym = node.getReferencedSym();
        if (sym instanceof JFieldSymbol) {
            JFieldSymbol fieldSym = (JFieldSymbol) sym;
            if (((JFieldSymbol) sym).getConstValue() == null) {
                checkMemberAccess((RuleContext) data, node, fieldSym);
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        JMethodSymbol symbol = (JMethodSymbol) node.getMethodType().getSymbol();
        checkMemberAccess((RuleContext) data, node, symbol);
        return null;
    }

    private void checkMemberAccess(RuleContext data, ASTExpression node, JAccessibleElementSymbol symbol) {
        checkMemberAccess(this, data, node, symbol, this.reportedNodes);
    }

    static void checkMemberAccess(AbstractRule rule, RuleContext data, ASTExpression refExpr, JAccessibleElementSymbol sym, Set<JavaNode> reportedNodes) {
        if (Modifier.isPrivate(sym.getModifiers())
            && !Objects.equals(sym.getEnclosingClass(),
                               refExpr.getEnclosingType().getSymbol())) {

            JavaNode node = sym.tryGetNode();
            assert node != null : "Node should be in the same compilation unit";
            if (reportedNodes.add(node)) {
                rule.addViolation(data, node, new String[] {stripPackageName(refExpr.getEnclosingType().getSymbol())});
            }
        }
    }

    /**
     * Returns the canonical name without the package name. Eg for a
     * canonical name {@code com.github.Outer.Inner}, returns {@code Outer.Inner}.
     */
    private static String stripPackageName(JClassSymbol symbol) {
        String p = symbol.getPackageName();
        String canoName = symbol.getCanonicalName();
        if (canoName == null) {
            return symbol.getSimpleName();
        }
        if (p.isEmpty()) {
            return canoName;
        }
        return canoName.substring(p.length() + 1); //+1 for the dot
    }
}
