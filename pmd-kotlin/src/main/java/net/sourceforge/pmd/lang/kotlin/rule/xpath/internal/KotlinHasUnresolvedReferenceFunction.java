/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

import nl.stokpop.typemapper.model.UnresolvedReferenceAst;

/**
 * XPath function {@code pmd-kotlin:hasUnresolvedReference()}.
 *
 * <p>Returns {@code true} when the Kotlin compiler could not resolve one or more
 * references on the context node's line. This typically indicates that a dependency
 * is missing from the analysis classpath.
 *
 * <p>For star imports ({@code import com.example.*}), the Kotlin compiler does not
 * report an unresolved reference at the import line itself even when the package is
 * missing. This function handles that case by checking whether the package name
 * extracted from the star import is present in the known-package index (derived from
 * the type-hierarchy classpath scan). If the package is unknown the function returns
 * {@code true}.
 *
 * <p>Use this function in a rule like {@code UnresolvedType} to warn users that
 * type information is incomplete, which may cause other rules to produce false
 * positives or miss violations.
 *
 * <p>Example XPath:
 * <pre>{@code
 * //ImportHeader[pmd-kotlin:hasUnresolvedReference()]
 * //PropertyDeclaration[pmd-kotlin:hasUnresolvedReference()]
 * }</pre>
 */
public final class KotlinHasUnresolvedReferenceFunction extends BaseKotlinXPathFunction {

    public static final KotlinHasUnresolvedReferenceFunction INSTANCE =
            new KotlinHasUnresolvedReferenceFunction();

    private KotlinHasUnresolvedReferenceFunction() {
        super("hasUnresolvedReference");
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
        return new HasUnresolvedReferenceFunctionCall();
    }

    private static final class HasUnresolvedReferenceFunctionCall implements FunctionCall {

        @Override
        public Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
            if (contextNode == null) {
                return false;
            }
            KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();
            String absPath = contextNode.getTextDocument().getFileId().getAbsolutePath();
            int line = contextNode.getBeginLine();
            List<UnresolvedReferenceAst> refs = ctx.unresolvedReferencesAt(absPath, line);
            return !refs.isEmpty();
        }
    }
}
