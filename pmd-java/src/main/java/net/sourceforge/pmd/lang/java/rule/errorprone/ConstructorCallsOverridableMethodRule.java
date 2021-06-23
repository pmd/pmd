/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.pcollections.PVector;
import org.pcollections.TreePVector;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
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

    private final Map<JMethodSymbol, Boolean> safeMethods = new HashMap<>();

    public ConstructorCallsOverridableMethodRule() {
        super(ASTConstructorDeclaration.class);
    }

    @Override
    public void start(RuleContext ctx) {
        super.start(ctx);
        safeMethods.clear();
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (node.getEnclosingType().isFinal()) {
            return null; // then cannot be overridden
        }
        node.getBody().descendants(ASTMethodCall.class)
            .filter(this::isOverridableCallOnThisInstance)
            .take(1)
            .forEach(it -> addViolation(data, it, PrettyPrintingUtil.prettyPrintOverload(it)));
        return null;
    }

    private boolean isOverridableCallOnThisInstance(ASTMethodCall call) {
        return isUnsafeCall(call, TreePVector.empty());
    }

    private boolean isUnsafeCall(ASTMethodCall call, PVector<ASTMethodDeclaration> recursionGuard) {
        if (!isCallOnThisInstance(call)) {
            return false;
        }

        OverloadSelectionResult overload = call.getOverloadSelectionInfo();
        if (overload.isFailed()) {
            return false;
        }

        JMethodSymbol method = (JMethodSymbol) overload.getMethodType().getSymbol();
        return isOverridable(method) || !hasSafeBody(method, recursionGuard);
    }

    private static boolean isCallOnThisInstance(ASTMethodCall call) {
        ASTExpression qualifier = call.getQualifier();
        return qualifier == null || JavaRuleUtil.isUnqualifiedThis(qualifier);
    }

    private boolean hasSafeBody(JMethodSymbol method, PVector<ASTMethodDeclaration> recursionGuard) {
        if (method.isStatic()) {
            return true; // no access to this instance anyway
        }
        // we need to prove that all calls on this instance are safe

        ASTMethodDeclaration declaration = method.tryGetNode();
        if (declaration == null) {
            return true; // no idea
        } else if (recursionGuard.contains(declaration)) {
            // being visited, assume body is safe
            return true;
        }

        // note we can't use computeIfAbsent because of comodification
        Boolean cachedResult = safeMethods.get(method);
        if (cachedResult != null) {
            return cachedResult;
        }

        PVector<ASTMethodDeclaration> deeperRecursion = recursionGuard.plus(declaration);
        boolean newResult = NodeStream.of(declaration.getBody())
                                      .descendants(ASTMethodCall.class)
                                      .filter(ConstructorCallsOverridableMethodRule::isCallOnThisInstance)
                                      .none(it -> isUnsafeCall(it, deeperRecursion));

        safeMethods.put(method, newResult);
        return newResult;
    }

    private static boolean isOverridable(JExecutableSymbol method) {
        // (assuming enclosing type is not final)
        // neither final nor private nor static
        return ((Modifier.FINAL | Modifier.PRIVATE | Modifier.STATIC) & method.getModifiers()) == 0;
    }
}
