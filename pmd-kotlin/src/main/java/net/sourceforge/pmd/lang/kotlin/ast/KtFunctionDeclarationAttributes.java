/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.types.KotlinTypeMapper;

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
     * Returns the resolved return type name of this function declaration,
     * or {@code null} when type analysis has not been run.
     */
    public @Nullable String getReturnTypeName() {
        return KotlinTypeMapper.getReturnTypeName(node);
    }
}
