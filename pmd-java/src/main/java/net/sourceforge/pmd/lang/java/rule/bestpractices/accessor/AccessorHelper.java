/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.accessor;

import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symbols.JAccessibleElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.reporting.RuleContext;

public class AccessorHelper {

    public static void checkMemberAccess(RuleContext ruleContext, JavaNode refExpr, JAccessibleElementSymbol sym,
                                         Set<JavaNode> reportedNodes) {
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
        return canoName == null
            ? symbol.getSimpleName()
            : symbol.getPackageName().isEmpty()
            ? canoName
            : canoName.substring(symbol.getPackageName().length() + 1); //+1 for the dot
    }
}
