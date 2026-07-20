/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.kotlin.types.KotlinNodeTypeData;

/**
 * Marks {@link AttributeView} subclasses that expose a {@code @TypeName} XPath attribute.
 * Only node types that actually carry a resolved type name implement this interface.
 *
 * @since 7.27.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public interface HasTypeName extends KotlinNode {
    KotlinNode getNode();

    /**
     * Returns the resolved type name stored on this node by the kotlin-type-mapper
     * pre-analysis pass, or {@code null} when type analysis has not been run or
     * the type could not be resolved.
     */
    default @Nullable String getTypeName() {
        return KotlinNodeTypeData.getTypeName(getNode());
    }
}
