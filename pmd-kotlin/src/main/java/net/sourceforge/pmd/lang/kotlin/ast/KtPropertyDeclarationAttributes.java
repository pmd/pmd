/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * @since 7.25.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public class KtPropertyDeclarationAttributes extends AttributeView<KotlinParser.KtPropertyDeclaration> implements HasTypeName {
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
}
