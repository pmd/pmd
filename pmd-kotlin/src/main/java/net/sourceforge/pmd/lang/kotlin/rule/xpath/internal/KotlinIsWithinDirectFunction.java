/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

/**
 * XPath function {@code pmd-kotlin:isWithinDirect(context)}.
 *
 * <p>Like {@link KotlinIsWithinFunction isWithin()}, but only returns {@code true}
 * when the context node is a <em>direct</em> member of the named structural context —
 * i.e. no function body or lambda literal lies between the node and the target context.
 *
 * <p>Supported context strings: {@code companion-object}, {@code top-level},
 * {@code object-declaration}, {@code function-body}, {@code lambda}.
 *
 * <p>Example: distinguish a member property of a companion object from a local
 * variable inside a function that happens to be declared in the companion object:
 * <pre>{@code
 * companion object {
 *     val formatter = DateTimeFormatter.ofPattern("…")  // isWithinDirect('companion-object') = true
 *     fun f() {
 *         val local = DateTimeFormatter.ofPattern("…")  // isWithinDirect('companion-object') = false
 *     }
 * }
 * }</pre>
 */
public final class KotlinIsWithinDirectFunction extends BaseKotlinXPathFunction {

    public static final KotlinIsWithinDirectFunction INSTANCE = new KotlinIsWithinDirectFunction();

    /** Ancestor node names that introduce a new function/lambda scope boundary. */
    private static final Set<String> SCOPE_BOUNDARIES =
            Collections.unmodifiableSet(new HashSet<>(Arrays.asList("FunctionBody", "LambdaLiteral")));

    private KotlinIsWithinDirectFunction() {
        super("isWithinDirect");
    }

    @Override
    public Type[] getArgumentTypes() {
        return new Type[]{Type.SINGLE_STRING};
    }

    @Override
    public Type getResultType() {
        return Type.SINGLE_BOOLEAN;
    }

    @Override
    public boolean dependsOnContext() {
        return true;
    }

    @Override
    public FunctionCall makeCallExpression() {
        return new IsWithinDirectFunctionCall();
    }

    private static final class IsWithinDirectFunctionCall implements FunctionCall {
        @Override
        public Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
            if (contextNode == null) {
                return false;
            }
            String context = (String) arguments[0];
            switch (context) {
                case "companion-object":
                    return isDirectWithin(contextNode, "CompanionObject");
                case "top-level":
                    // top-level is inherently direct: no class/object/function ancestor at all
                    return isTopLevelDirect(contextNode);
                case "object-declaration":
                    return isDirectWithin(contextNode, "ObjectDeclaration");
                case "function-body":
                    return isDirectInsideFunctionBody(contextNode);
                case "lambda":
                    return isDirectInsideLambda(contextNode);
                default:
                    throw new XPathFunctionException(
                            "pmd-kotlin:isWithinDirect() — unknown context '" + context
                            + "'. Supported: companion-object, top-level, object-declaration, function-body, lambda");
            }
        }

        /**
         * Walk ancestors. Return true if we find {@code targetName} before hitting any
         * scope boundary (FunctionBody, LambdaLiteral).
         */
        private static boolean isDirectWithin(Node node, String targetName) {
            Node ancestor = node.getParent();
            while (ancestor != null) {
                String name = ancestor.getXPathNodeName();
                if (targetName.equals(name)) {
                    return true;
                }
                if (SCOPE_BOUNDARIES.contains(name)) {
                    return false;
                }
                ancestor = ancestor.getParent();
            }
            return false;
        }

        /**
         * True when no class-like or function-like ancestor exists — same as isWithin top-level
         * since top-level declarations are never inside any scope.
         */
        private static boolean isTopLevelDirect(Node node) {
            Node ancestor = node.getParent();
            while (ancestor != null) {
                String name = ancestor.getXPathNodeName();
                if ("ClassDeclaration".equals(name)
                        || "ObjectDeclaration".equals(name)
                        || "CompanionObject".equals(name)
                        || "FunctionDeclaration".equals(name)
                        || "LambdaLiteral".equals(name)) {
                    return false;
                }
                ancestor = ancestor.getParent();
            }
            return true;
        }

        /**
         * True when the nearest FunctionBody ancestor is found before any LambdaLiteral.
         */
        private static boolean isDirectInsideFunctionBody(Node node) {
            Node ancestor = node.getParent();
            while (ancestor != null) {
                String name = ancestor.getXPathNodeName();
                if ("FunctionBody".equals(name)) {
                    return true;
                }
                if ("LambdaLiteral".equals(name)) {
                    return false;
                }
                ancestor = ancestor.getParent();
            }
            return false;
        }

        /**
         * True when the nearest LambdaLiteral ancestor is found before any FunctionBody
         * or another LambdaLiteral (i.e. the node is a direct member of that lambda,
         * not inside a nested lambda).
         */
        private static boolean isDirectInsideLambda(Node node) {
            Node ancestor = node.getParent();
            while (ancestor != null) {
                String name = ancestor.getXPathNodeName();
                if ("LambdaLiteral".equals(name)) {
                    return true;
                }
                if ("FunctionBody".equals(name)) {
                    return false;
                }
                ancestor = ancestor.getParent();
            }
            return false;
        }
    }
}
