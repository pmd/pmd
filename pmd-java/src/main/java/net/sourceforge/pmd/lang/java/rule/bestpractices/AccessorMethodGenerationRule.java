/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
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
        checkMemberAccessIfConstValueIsNull(node, (RuleContext) data, node.getReferencedSym());
        return null;
    }

    @Override
    public Object visit(ASTVariableAccess node, Object data) {
        checkMemberAccessIfConstValueIsNull(node, (RuleContext) data, node.getReferencedSym());
        return null;
    }

    private void checkMemberAccessIfConstValueIsNull(ASTExpression node, RuleContext data, JVariableSymbol sym) {
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
        checkMemberAccess(data, node, symbol, reportedNodes);
    }

    static void checkMemberAccess(RuleContext ruleContext, JavaNode refExpr, JAccessibleElementSymbol sym, Set<JavaNode> reportedNodes) {
        if (Modifier.isPrivate(sym.getModifiers())
            && !Objects.equals(sym.getEnclosingClass(),
                               refExpr.getEnclosingType().getSymbol())) {

            JavaNode node = sym.tryGetNode();
            if (node == null && JConstructorSymbol.CTOR_NAME.equals(sym.getSimpleName())) {
                // might be a default constructor, implicitly defined and not explicitly in the compilation unit
                node = sym.getEnclosingClass().tryGetNode();
            }
            assert node != null : "Node should be in the same compilation unit";
            if (reportedNodes.add(node)) {
                ruleContext.addViolation(node, stripPackageName(refExpr.getEnclosingType().getSymbol()));
            }
        }
    }

    /**
     * Returns the canonical name without the package name. Eg for a
     * canonical name {@code com.github.Outer.Inner}, returns {@code Outer.Inner}.
     */
    private static String stripPackageName(JClassSymbol symbol) {
        String canoName = symbol.getCanonicalName();
        if (canoName == null) {
            return symbol.getSimpleName();
        }
        String p = symbol.getPackageName();
        if (p.isEmpty()) {
            return canoName;
        }
        return canoName.substring(p.length() + 1); //+1 for the dot
    }
}
