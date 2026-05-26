/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.ast.internal.KotlinAstUtil;

/**
 * @since 7.25.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public interface HasSimpleIdentifier extends KotlinNode {
    KotlinNode getNode();

    /**
     * Returns the text of the first {@code SimpleIdentifier} direct child,
     * or {@code null} if none is present.
     */
    default String getIdentifier() {
        return KotlinAstUtil.textOf(getNode().firstChild(KotlinParser.KtSimpleIdentifier.class));
    }
}
