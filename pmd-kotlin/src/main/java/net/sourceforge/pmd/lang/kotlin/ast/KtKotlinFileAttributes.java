/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.types.KotlinNodeTypeData;

/**
 * XPath attributes for the {@code KotlinFile} root node.
 *
 * <p>{@code @TypeInfoAvailable} is present and {@code true} when the kotlin-type-mapper
 * pre-analysis completed successfully for this file; the attribute is absent when no
 * classpath was available or analysis failed. Use {@code [@TypeInfoAvailable]} as a
 * truthy check, or {@code [not(@TypeInfoAvailable)]} to detect the no-type-info case.
 *
 * @since 7.25.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public class KtKotlinFileAttributes extends AttributeView<KotlinParser.KtKotlinFile> {

    public KtKotlinFileAttributes(KotlinParser.KtKotlinFile node) {
        super(node);
    }

    /**
     * Returns {@code Boolean.TRUE} when type analysis ran successfully for this file,
     * {@code null} otherwise. Returning {@code null} causes the attribute to be absent
     * from XPath, so {@code [@TypeInfoAvailable]} is a clean truthy test.
     */
    public Boolean isTypeInfoAvailable() {
        return KotlinNodeTypeData.isTypeInfoAvailable(getNode()) ? Boolean.TRUE : null;
    }
}
