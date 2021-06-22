/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;

/**
 * Searches through all methods and constructors called from constructors. It
 * marks as dangerous any call to overridable methods from non-private
 * constructors. It marks as dangerous any calls to dangerous private
 * constructors from non-private constructors.
 *
 * @author CL Gilbert (dnoyeb@users.sourceforge.net)
 *
 *         TODO match parameter types. Aggressively strips off any package
 *         names. Normal compares the names as is. TODO What about interface
 *         declarations which can have internal classes
 */
public final class ConstructorCallsOverridableMethodRule extends AbstractJavaRulechainRule {

    public ConstructorCallsOverridableMethodRule() {
        super(ASTConstructorDeclaration.class);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (node.getEnclosingType().isFinal()) {
            return null; // then cannot be overridden
        }
        node.getBody().descendants(ASTMethodCall.class)
            .filter(ConstructorCallsOverridableMethodRule::isOverridableCallOnThisInstance)
            .take(1)
            .forEach(it -> addViolation(data, it, PrettyPrintingUtil.prettyPrintOverload(it)));
        return null;
    }

    private static boolean isOverridableCallOnThisInstance(ASTMethodCall call) {
        ASTExpression qualifier = call.getQualifier();
        if (qualifier != null && !JavaRuleUtil.isUnqualifiedThis(qualifier)) {
            return false;
        }

        OverloadSelectionResult overload = call.getOverloadSelectionInfo();
        return !overload.isFailed()
            && isOverridable(overload.getMethodType().getSymbol());

    }

    private static boolean isOverridable(JExecutableSymbol method) {
        // (assuming enclosing type is not final)
        // neither final nor private nor static
        return ((Modifier.FINAL | Modifier.PRIVATE | Modifier.STATIC) & method.getModifiers()) == 0;
    }
}
