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

    private static final String MESSAGE = "Overridable method called during object construction: {0} ";
    private static final String MESSAGE_TRANSITIVE = "This method may call an overridable method during object construction: {0}";


    // Maps methods to the method that makes them unsafe
    // Safe methods are mapped to null
    // The value is used for better messages
    private final Map<JMethodSymbol, JMethodSymbol> safeMethods = new HashMap<>();

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
        for (ASTMethodCall call : node.getBody().descendants(ASTMethodCall.class)) {
            JMethodSymbol unsafetyReason = getUnsafetyReason(call, TreePVector.empty());
            if (unsafetyReason != null) {
                String message;
                if (unsafetyReason == call.getOverloadSelectionInfo().getMethodType().getSymbol()) {
                    message = MESSAGE;
                } else {
                    message = MESSAGE_TRANSITIVE;
                }
                addViolationWithMessage(data, call, message, new Object[] { PrettyPrintingUtil.prettyPrintOverload(call) });
            }
        }
        return null;
    }

    private JMethodSymbol getUnsafetyReason(ASTMethodCall call, PVector<ASTMethodDeclaration> recursionGuard) {
        if (!isCallOnThisInstance(call)) {
            return null;
        }

        OverloadSelectionResult overload = call.getOverloadSelectionInfo();
        if (overload.isFailed()) {
            return null;
        }

        JMethodSymbol method = (JMethodSymbol) overload.getMethodType().getSymbol();
        if (isOverridable(method)) {
            return method; // the method itself
        } else {
            return getUnsafetyReason(method, recursionGuard);
        }
    }

    private JMethodSymbol getUnsafetyReason(JMethodSymbol method, PVector<ASTMethodDeclaration> recursionGuard) {
        if (method.isStatic()) {
            return null; // no access to this instance anyway
        }
        // we need to prove that all calls on this instance are safe

        ASTMethodDeclaration declaration = method.tryGetNode();
        if (declaration == null) {
            return null; // no idea
        } else if (recursionGuard.contains(declaration)) {
            // being visited, assume body is safe
            return null;
        }

        // note we can't use computeIfAbsent because of comodification
        if (safeMethods.containsKey(method)) {
            return safeMethods.get(method);
        }

        PVector<ASTMethodDeclaration> deeperRecursion = recursionGuard.plus(declaration);

        for (ASTMethodCall call : NodeStream.of(declaration.getBody())
                                            .descendants(ASTMethodCall.class)
                                            .filter(ConstructorCallsOverridableMethodRule::isCallOnThisInstance)) {
            JMethodSymbol unsafetyReason = getUnsafetyReason(call, deeperRecursion);
            if (unsafetyReason != null) {
                // this method call is unsafe for some reason,
                // body is unsafe for the same reason
                safeMethods.put(method, unsafetyReason);
                return unsafetyReason;
            }
        }

        // body is safe
        safeMethods.put(method, null);
        return null;
    }

    private static boolean isCallOnThisInstance(ASTMethodCall call) {
        ASTExpression qualifier = call.getQualifier();
        return qualifier == null || JavaRuleUtil.isUnqualifiedThis(qualifier);
    }

    private static boolean isOverridable(JExecutableSymbol method) {
        // (assuming enclosing type is not final)
        // neither final nor private nor static
        return ((Modifier.FINAL | Modifier.PRIVATE | Modifier.STATIC) & method.getModifiers()) == 0;
    }
}
