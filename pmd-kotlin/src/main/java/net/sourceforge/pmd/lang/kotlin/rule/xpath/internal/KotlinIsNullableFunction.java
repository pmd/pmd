/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

import nl.stokpop.typemapper.model.DeclarationAst;

/**
 * XPath function {@code pmd-kotlin:isNullable()}.
 *
 * <p>Returns {@code true} when the context node's resolved type is nullable, i.e. the
 * type name ends with {@code ?}.
 *
 * <p>Supported node types (same as {@code typeIs}):
 * <ul>
 *   <li>{@code PropertyDeclaration} -- property / local variable type</li>
 *   <li>{@code FunctionDeclaration} -- return type</li>
 *   <li>{@code FunctionValueParameter} / {@code ClassParameter} -- parameter type</li>
 *   <li>{@code CatchBlock} -- caught exception type</li>
 *   <li>{@code ForStatement} -- loop variable type</li>
 * </ul>
 *
 * <p>Example XPath:
 * <pre>{@code
 * //FunctionDeclaration[pmd-kotlin:isNullable() and pmd-kotlin:typeIs('java.util.Collection')]
 * //PropertyDeclaration[pmd-kotlin:isNullable()]
 * }</pre>
 */
public final class KotlinIsNullableFunction extends BaseKotlinXPathFunction {

    public static final KotlinIsNullableFunction INSTANCE = new KotlinIsNullableFunction();

    private KotlinIsNullableFunction() {
        super("isNullable");
    }

    @Override
    public Type[] getArgumentTypes() {
        return new Type[0];
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
        return new IsNullableFunctionCall();
    }

    private static boolean isNullableType(@Nullable String typeName) {
        return typeName != null && typeName.endsWith("?");
    }

    private static final class IsNullableFunctionCall implements FunctionCall {
        @Override
        public Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
            if (contextNode == null) {
                return false;
            }

            if (contextNode instanceof KotlinNode) {
                KotlinNode kotlinNode = (KotlinNode) contextNode;
                if (isNullableType(kotlinNode.getTypeName())
                        || isNullableType(kotlinNode.getReturnTypeName())) {
                    return true;
                }
            }

            KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
            String absPath = contextNode.getTextDocument().getFileId().getAbsolutePath();
            int line = contextNode.getBeginLine();
            List<DeclarationAst> decls = ctx.declarationsAt(absPath, line);
            for (DeclarationAst decl : decls) {
                if (isNullableType(decl.getType()) || isNullableType(decl.getReturnType())) {
                    return true;
                }
            }
            return false;
        }
    }
}
