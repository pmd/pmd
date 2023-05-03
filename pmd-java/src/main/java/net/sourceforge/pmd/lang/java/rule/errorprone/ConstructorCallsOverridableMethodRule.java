/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.lang.reflect.Modifier;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
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
    private static final String MESSAGE_TRANSITIVE = "This method may call an overridable method during object construction: {0} (call stack: [{1}])";


    // Maps methods to the method call stack that makes them unsafe
    // Safe methods are mapped to an empty stack
    // The method call stack (value of the map) is used for better messages
    private final Map<JMethodSymbol, Deque<JMethodSymbol>> safeMethods = new HashMap<>();

    private static final Deque<JMethodSymbol> EMPTY_STACK = new LinkedList<>();

    private static final Set<String> MAKE_FIELD_FINAL_CLASS_ANNOT =
        setOf(
            "lombok.Value"
        );

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
        if (node.getEnclosingType().isFinal() || JavaAstUtils.hasAnyAnnotation(node.getEnclosingType(), MAKE_FIELD_FINAL_CLASS_ANNOT)) {
            return null; // then cannot be overridden
        }
        for (ASTMethodCall call : node.getBody().descendants(ASTMethodCall.class)) {
            Deque<JMethodSymbol> unsafetyReason = getUnsafetyReason(call, TreePVector.empty());
            if (!unsafetyReason.isEmpty()) {
                JMethodSig overload = call.getOverloadSelectionInfo().getMethodType();
                JMethodSig unsafeMethod = call.getTypeSystem().sigOf(unsafetyReason.getLast());
                String message = unsafeMethod.equals(overload) ? MESSAGE : MESSAGE_TRANSITIVE;
                String lastMethod = PrettyPrintingUtil.prettyPrintOverload(unsafetyReason.getLast());
                String stack = unsafetyReason.stream().map(PrettyPrintingUtil::prettyPrintOverload).collect(Collectors.joining(", "));
                asCtx(data).addViolationWithMessage(call, message, lastMethod, stack);
            }
        }
        return null;
    }

    @NonNull
    private Deque<JMethodSymbol> getUnsafetyReason(ASTMethodCall call, PVector<ASTMethodDeclaration> recursionGuard) {
        if (!isCallOnThisInstance(call)) {
            return EMPTY_STACK;
        }

        OverloadSelectionResult overload = call.getOverloadSelectionInfo();
        if (overload.isFailed()) {
            return EMPTY_STACK;
        }

        JMethodSymbol method = (JMethodSymbol) overload.getMethodType().getSymbol();
        if (isOverridable(method)) {
            Deque<JMethodSymbol> stack = new LinkedList<>();
            stack.addFirst(method); // the method itself
            return stack;
        } else {
            return getUnsafetyReason(method, recursionGuard);
        }
    }

    @NonNull
    private Deque<JMethodSymbol> getUnsafetyReason(JMethodSymbol method, PVector<ASTMethodDeclaration> recursionGuard) {
        if (method.isStatic()) {
            return EMPTY_STACK; // no access to this instance anyway
        }
        // we need to prove that all calls on this instance are safe

        ASTMethodDeclaration declaration = method.tryGetNode();
        if (declaration == null) {
            return EMPTY_STACK; // no idea
        } else if (recursionGuard.contains(declaration)) {
            // being visited, assume body is safe
            return EMPTY_STACK;
        }

        // note we can't use computeIfAbsent because of comodification
        if (safeMethods.containsKey(method)) {
            return safeMethods.get(method);
        }

        PVector<ASTMethodDeclaration> deeperRecursion = recursionGuard.plus(declaration);

        for (ASTMethodCall call : NodeStream.of(declaration.getBody())
                                            .descendants(ASTMethodCall.class)
                                            .filter(ConstructorCallsOverridableMethodRule::isCallOnThisInstance)) {
            Deque<JMethodSymbol> unsafetyReason = getUnsafetyReason(call, deeperRecursion);
            if (!unsafetyReason.isEmpty()) {
                // this method call is unsafe for some reason,
                // body is unsafe for the same reason
                safeMethods.putIfAbsent(method, new LinkedList<>(unsafetyReason));
                safeMethods.get(method).addFirst(method);
                return safeMethods.get(method);
            }
        }

        // body is safe
        safeMethods.remove(method);
        return EMPTY_STACK;
    }

    private static boolean isCallOnThisInstance(ASTMethodCall call) {
        ASTExpression qualifier = call.getQualifier();
        return qualifier == null || JavaAstUtils.isUnqualifiedThis(qualifier);
    }

    private static boolean isOverridable(JExecutableSymbol method) {
        // (assuming enclosing type is not final)
        // neither final nor private nor static
        return ((Modifier.FINAL | Modifier.PRIVATE | Modifier.STATIC) & method.getModifiers()) == 0;
    }
}
