/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * @since 7.27.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public class KtSingleAnnotationAttributes extends AttributeView<KotlinParser.KtSingleAnnotation> implements HasTypeName {
    public KtSingleAnnotationAttributes(KotlinParser.KtSingleAnnotation node) {
        super(node);
    }
}
