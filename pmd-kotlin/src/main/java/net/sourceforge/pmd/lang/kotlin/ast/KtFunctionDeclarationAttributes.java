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
public class KtFunctionDeclarationAttributes extends AttributeView<KotlinParser.KtFunctionDeclaration> implements HasSimpleIdentifier, HasModifiers {
    public KtFunctionDeclarationAttributes(KotlinParser.KtFunctionDeclaration node) {
        super(node);
    }
}
