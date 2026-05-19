/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionDefinition;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

import nl.stokpop.typemapper.model.CallSiteAst;
import nl.stokpop.typemapper.model.DeclarationAst;

abstract class AbstractKotlinTypeIsFunctionCall implements XPathFunctionDefinition.FunctionCall {

    @Override
    public final Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
        if (contextNode == null) {
            return false;
        }

        String typeName = (String) arguments[0];
        KotlinTypeAnalysisContext ctx = KotlinTypeAnalysisContextHolder.get();

        if (contextNode instanceof KotlinNode) {
            KotlinNode kn = (KotlinNode) contextNode;
            if (matchesNodeAttribute(kn, typeName, ctx)) {
                return true;
            }
            // If the node has an explicit type annotation (set by KotlinTypeAnnotationVisitor)
            // that didn't match, trust the annotation and do not fall through to the call site
            // index. This prevents RHS constructor calls from matching when the declared type
            // on the same line is a different (e.g. interface) type.
            if (kn.getTypeName() != null || kn.getReturnTypeName() != null) {
                return false;
            }
        }

        // No type annotation on the node itself -- look up the declaration/call-site index.
        // Always try both: declarationsAt() uses +/-1 line tolerance and may surface an
        // adjacent declaration (e.g. a function header) that does not match, so we must
        // not short-circuit on a miss and must still check call sites.
        String absPath = contextNode.getTextDocument().getFileId().getAbsolutePath();
        int line = contextNode.getBeginLine();
        List<DeclarationAst> decls = ctx.declarationsAt(absPath, line);
        return matchesAnyDeclaration(decls, typeName, ctx)
                || matchesAnyCallSite(ctx.callSitesAt(absPath, line), typeName, ctx);
    }

    protected abstract boolean matchesNodeAttribute(KotlinNode node, String typeName, KotlinTypeAnalysisContext ctx);

    protected abstract boolean matchesType(String expectedType, String actualType, KotlinTypeAnalysisContext ctx);

    private boolean matchesAnyDeclaration(List<DeclarationAst> decls, String typeName, KotlinTypeAnalysisContext ctx) {
        for (DeclarationAst decl : decls) {
            String type = decl.getType();
            if (type != null && matchesType(typeName, type, ctx)) {
                return true;
            }
            String returnType = decl.getReturnType();
            if (returnType != null && matchesType(typeName, returnType, ctx)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesAnyCallSite(List<CallSiteAst> calls, String typeName, KotlinTypeAnalysisContext ctx) {
        for (CallSiteAst call : calls) {
            if (matchesType(typeName, call.getReturnType(), ctx)) {
                return true;
            }
        }
        return false;
    }
}
