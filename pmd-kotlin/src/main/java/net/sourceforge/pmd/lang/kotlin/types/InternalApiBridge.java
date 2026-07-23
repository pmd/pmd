/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.rule.internal.KotlinTypeAnalysisContext;

/**
 * Internal API bridge exposing package-private setters of {@link KotlinNodeTypeData}
 * to callers outside the {@code types} package (e.g. {@code KotlinLanguageProcessor}).
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 *
 * @since 7.27.0
 * @internalApi None of this is published API, and compatibility can be broken anytime!
 */
@InternalApi
public final class InternalApiBridge {

    private InternalApiBridge() {}

    /** @see KotlinNodeTypeData#setTypeName(KotlinNode, String) */
    public static void setTypeName(KotlinNode node, String typeName) {
        KotlinNodeTypeData.setTypeName(node, typeName);
    }

    /** @see KotlinNodeTypeData#setReturnTypeName(KotlinNode, String) */
    public static void setReturnTypeName(KotlinNode node, String returnTypeName) {
        KotlinNodeTypeData.setReturnTypeName(node, returnTypeName);
    }

    public static void setAnnotationFqNames(KotlinNode node, List<String> fqnList) {
        KotlinNodeTypeData.setAnnotationFqNames(node, fqnList);
    }

    /** @see KotlinNodeTypeData#setTypeInfoAvailable(KtKotlinFile) */
    public static void setTypeInfoAvailable(KtKotlinFile rootNode) {
        KotlinNodeTypeData.setTypeInfoAvailable(rootNode);
    }

    /** @see KotlinNodeTypeData#setAnalysisContext(KtKotlinFile, KotlinTypeAnalysisContext) */
    public static void setAnalysisContext(KtKotlinFile rootNode, KotlinTypeAnalysisContext ctx) {
        KotlinNodeTypeData.setAnalysisContext(rootNode, ctx);
    }
}
