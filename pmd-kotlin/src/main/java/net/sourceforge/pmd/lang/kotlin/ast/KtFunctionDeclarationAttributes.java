/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.types.KotlinNodeTypeData;
import net.sourceforge.pmd.lang.kotlin.types.KotlinTypeName;

/**
 * @since 7.25.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public class KtFunctionDeclarationAttributes extends AttributeView<KotlinParser.KtFunctionDeclaration> implements HasSimpleIdentifier, HasModifiers {
    public KtFunctionDeclarationAttributes(KotlinParser.KtFunctionDeclaration node) {
        super(node);
    }

    /**
     * Returns the resolved return type of this function declaration,
     * or {@code null} when type analysis has not been run.
     */
    public @Nullable KotlinTypeName getReturnType() {
        return KotlinNodeTypeData.getReturnType(node);
    }

    /**
     * Returns the resolved return type name as a string for the
     * {@code @ReturnTypeName} XPath attribute, or {@code null} when type
     * analysis has not been run.
     */
    public @Nullable String getReturnTypeName() {
        return KotlinTypeName.displayStringOf(getReturnType());
    }

    /**
     * Returns fully-qualified annotation class names for this function declaration.
     * Returns an empty list when no annotation names are available.
     *
     * <p>This method follows the same pattern as pmd-java's
     * {@code AnnotableSymbol.getDeclaredAnnotations()},
     * where collections are always non-null: an empty collection means "no annotations present"
     * rather than "unknown". Empty lists distinguish between unavailable information and genuinely
     * no annotations.
     */
    public List<String> getAnnotationFqNames() {
        return KotlinNodeTypeData.getAnnotationFqNames(node);
    }
}
