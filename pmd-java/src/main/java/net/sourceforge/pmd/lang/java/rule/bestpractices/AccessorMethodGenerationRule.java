/**
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
        JFieldSymbol sym = node.getReferencedSym();
        if (sym != null && sym.getConstValue() == null) {
            checkMemberAccess((RuleContext) data, node, sym);
        }
        return null;
    }

    @Override
    public Object visit(ASTVariableAccess node, Object data) {
        JVariableSymbol sym = node.getReferencedSym();
        if (sym instanceof JFieldSymbol && ((JFieldSymbol) sym).getConstValue() == null) {
            checkMemberAccess((RuleContext) data, node, (JFieldSymbol) sym);
        }
        return null;
    }

    @Override
    public Object visit(ASTMethodCall node, Object data) {
        checkMemberAccess((RuleContext) data, node, node.getMethodType().getSymbol());
        return null;
    }

    private void checkMemberAccess(RuleContext data, ASTExpression node, JAccessibleElementSymbol symbol) {
        if (Modifier.isPrivate(symbol.getModifiers())
                && !Objects.equals(symbol.getEnclosingClass(),
                node.getEnclosingType().getSymbol())) {

            JavaNode node1 = symbol.tryGetNode();
            if (node1 == null && JConstructorSymbol.CTOR_NAME.equals(symbol.getSimpleName())) {
                // might be a default constructor, implicitly defined and not explicitly in the compilation unit
                node1 = symbol.getEnclosingClass().tryGetNode();
            }
            assert node1 != null : "Node should be in the same compilation unit";
            if (reportedNodes.add(node1)) {
                data.addViolation(node1, stripPackageName(node.getEnclosingType().getSymbol()));
            }
        }
    }

    /**
     * Returns the canonical name without the package name. Eg for a
     * canonical name {@code com.github.Outer.Inner}, returns {@code Outer.Inner}.
     */
    private static String stripPackageName(JClassSymbol symbol) {
        String canoName = symbol.getCanonicalName();
        return canoName == null
                ? symbol.getSimpleName()
                : symbol.getPackageName().isEmpty()
                ? canoName
                : canoName.substring(symbol.getPackageName().length() + 1); //+1 for the dot
    }
}
