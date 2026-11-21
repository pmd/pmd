/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * Attributes of the destructuring pattern {@code (a, b)} of {@code val (a, b) = x},
 * {@code for ((a, b) in xs)} and {@code { (a, b) -> }}. The {@code @TypeName} is the type of the
 * destructured value as a whole, e.g. {@code kotlin.Pair<kotlin.Int, kotlin.String>}; the type of
 * each component is available on its own {@code VariableDeclaration}.
 *
 * @since 7.30.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public class KtMultiVariableDeclarationAttributes extends AttributeView<KotlinParser.KtMultiVariableDeclaration> implements HasTypeName {
    public KtMultiVariableDeclarationAttributes(KotlinParser.KtMultiVariableDeclaration node) {
        super(node);
    }
}
