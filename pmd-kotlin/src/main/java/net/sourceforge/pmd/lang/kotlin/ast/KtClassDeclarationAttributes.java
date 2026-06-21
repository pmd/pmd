/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.types.KotlinNodeTypeData;

/**
 * @since 7.25.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public class KtClassDeclarationAttributes extends AttributeView<KotlinParser.KtClassDeclaration> implements HasSimpleIdentifier, HasModifiers, HasTypeName {
    public KtClassDeclarationAttributes(KotlinParser.KtClassDeclaration node) {
        super(node);
    }

    public @Nullable List<String> getAnnotationFqNames() {
        List<String> names = KotlinNodeTypeData.getAnnotationFqNames(node);
        return names.isEmpty() ? null : names;
    }
}
