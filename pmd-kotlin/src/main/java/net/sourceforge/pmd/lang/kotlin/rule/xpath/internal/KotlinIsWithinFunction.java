/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

/**
 * XPath function {@code pmd-kotlin:isWithin(context)}.
 *
 * <p>Returns {@code true} when the context node is nested (at any depth) inside
 * the named structural context. Supported context strings:
 * <ul>
 *   <li>{@code 'companion-object'} — inside a {@code companion object} body</li>
 *   <li>{@code 'top-level'} — directly under the file (not inside any class, object, or function)</li>
 *   <li>{@code 'object-declaration'} — inside a named {@code object} declaration (not companion)</li>
 *   <li>{@code 'function-body'} — inside a function body</li>
 *   <li>{@code 'lambda'} — inside a lambda literal</li>
 * </ul>
 *
 * <p>The check is location-only: whether the property is {@code val} or {@code var}
 * is separate and accessible via {@code @Mutable}.
 *
 * <p>Example — flag property declarations that are not safely constant:
 * <pre>{@code
 * //PropertyDeclaration[
 *     not(not(@Mutable) and pmd-kotlin:isWithin('companion-object'))
 *     and not(not(@Mutable) and pmd-kotlin:isWithin('top-level'))
 *     and not(not(@Mutable) and pmd-kotlin:isWithin('object-declaration'))
 * ]
 * }</pre>
 */
public final class KotlinIsWithinFunction extends BaseKotlinXPathFunction {

    public static final KotlinIsWithinFunction INSTANCE = new KotlinIsWithinFunction();

    private KotlinIsWithinFunction() {
        super("isWithin");
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
        return new IsWithinFunctionCall();
    }

    private static final class IsWithinFunctionCall implements FunctionCall {
        @Override
        public Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
            if (contextNode == null) {
                return false;
            }
            String context = (String) arguments[0];
            switch (context) {
                case "companion-object":
                    return isWithinCompanionObject(contextNode);
                case "top-level":
                    return isTopLevel(contextNode);
                case "object-declaration":
                    return isWithinObjectDeclaration(contextNode);
                case "function-body":
                    return isWithinFunctionBody(contextNode);
                case "lambda":
                    return isWithinLambda(contextNode);
                default:
                    throw new XPathFunctionException(
                            "pmd-kotlin:isWithin() — unknown context '" + context
                            + "'. Supported: companion-object, top-level, object-declaration, function-body, lambda");
            }
        }

        /** True when any ancestor is a CompanionObject. */
        private static boolean isWithinCompanionObject(Node node) {
            Node ancestor = node.getParent();
            while (ancestor != null) {
                if ("CompanionObject".equals(ancestor.getXPathNodeName())) {
                    return true;
                }
                ancestor = ancestor.getParent();
            }
            return false;
        }

        /**
         * True when the node is directly under the KotlinFile — i.e. no ClassDeclaration,
         * ObjectDeclaration, CompanionObject, or FunctionDeclaration ancestor exists.
         */
        private static boolean isTopLevel(Node node) {
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
         * True when any ancestor is an ObjectDeclaration (but not a CompanionObject,
         * which is a separate node type).
         */
        private static boolean isWithinObjectDeclaration(Node node) {
            Node ancestor = node.getParent();
            while (ancestor != null) {
                if ("ObjectDeclaration".equals(ancestor.getXPathNodeName())) {
                    return true;
                }
                ancestor = ancestor.getParent();
            }
            return false;
        }

        /** True when any ancestor is a FunctionBody. */
        private static boolean isWithinFunctionBody(Node node) {
            Node ancestor = node.getParent();
            while (ancestor != null) {
                if ("FunctionBody".equals(ancestor.getXPathNodeName())) {
                    return true;
                }
                ancestor = ancestor.getParent();
            }
            return false;
        }

        /** True when any ancestor is a LambdaLiteral. */
        private static boolean isWithinLambda(Node node) {
            Node ancestor = node.getParent();
            while (ancestor != null) {
                if ("LambdaLiteral".equals(ancestor.getXPathNodeName())) {
                    return true;
                }
                ancestor = ancestor.getParent();
            }
            return false;
        }
    }
}
