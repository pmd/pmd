/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.types.KotlinNodeTypeData;

/**
 * @since 7.25.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public class KtClassParameterAttributes extends AttributeView<KotlinParser.KtClassParameter> implements HasSimpleIdentifier, HasModifiers, HasTypeName {
    public KtClassParameterAttributes(KotlinParser.KtClassParameter node) {
        super(node);
    }

    /**
     * Returns fully-qualified annotation class names for this class parameter.
     * Returns an empty list when no annotation names are available.
     */
    public List<String> getAnnotationFqNames() {
        return KotlinNodeTypeData.getAnnotationFqNames(node);
    }
}
