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
public class KtForStatementAttributes extends AttributeView<KotlinParser.KtForStatement> implements HasTypeName {
    public KtForStatementAttributes(KotlinParser.KtForStatement node) {
        super(node);
    }
}
