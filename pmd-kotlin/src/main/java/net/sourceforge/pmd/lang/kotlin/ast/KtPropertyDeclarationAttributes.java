/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.types.KotlinNodeTypeData;

/**
 * @since 7.27.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public class KtPropertyDeclarationAttributes extends AttributeView<KotlinParser.KtPropertyDeclaration> implements HasModifiers, HasTypeName {
    public KtPropertyDeclarationAttributes(KotlinParser.KtPropertyDeclaration node) {
        super(node);
    }

    /**
     * Returns {@code true} if this property is declared with {@code var} (mutable),
     * {@code false} if declared with {@code val} (immutable).
     */
    public boolean isMutable() {
        return node.VAR() != null;
    }

    /**
     * Returns fully-qualified annotation class names for this property declaration.
     * Returns an empty list when no annotation names are available.
     */
    public List<String> getAnnotationFqNames() {
        return KotlinNodeTypeData.getAnnotationFqNames(node);
    }
}
