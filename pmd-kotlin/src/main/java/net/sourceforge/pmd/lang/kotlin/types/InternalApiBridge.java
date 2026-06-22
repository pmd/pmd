/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;

/**
 * Internal API bridge exposing package-private setters of {@link KotlinNodeTypeData}
 * to callers outside the {@code types} package (e.g. {@code KotlinLanguageProcessor}).
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 *
 * @since 7.26.0
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

    /** @see KotlinNodeTypeData#setAnnotationFqNames(KotlinNode, String) */
    public static void setAnnotationFqNames(KotlinNode node, String annotationFqNames) {
        KotlinNodeTypeData.setAnnotationFqNames(node, annotationFqNames);
    }

    /** @see KotlinNodeTypeData#setTypeInfoAvailable(KtKotlinFile) */
    public static void setTypeInfoAvailable(KtKotlinFile rootNode) {
        KotlinNodeTypeData.setTypeInfoAvailable(rootNode);
    }
}
