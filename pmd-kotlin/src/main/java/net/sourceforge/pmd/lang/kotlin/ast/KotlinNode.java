/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrNode;

/**
 * Supertype of all kotlin nodes.
 */
public interface KotlinNode extends AntlrNode<KotlinNode> {

    /**
     * Returns the explicit modifier keywords of this declaration node as a
     * space-separated string, e.g. {@code "override suspend"} or {@code "data open"}.
     * Returns {@code null} when this node has no {@code Modifiers} child or when
     * all children of {@code Modifiers} are annotations (annotations are excluded).
     * Exposed as XPath attribute {@code @Modifiers}.
     *
     * <p>Returns {@code null} (not empty string or Set) so that the XPath attribute is absent
     * on nodes without modifiers — consistent with PMD's {@code getImage()} convention and
     * the null-attribute filter that keeps the PMD Designer clean.
     * Visible in the PMD Designer only on nodes that carry at least one keyword modifier.
     * Implemented in {@link KotlinInnerNode}; returns {@code null} by default (e.g. on terminal nodes).
     */
    default @Nullable String getModifiers() {
        return null;
    }

    /**
     * Returns the text of the first {@code SimpleIdentifier} direct child of this node,
     * or {@code null} if there is none. This makes it easy to write XPath expressions
     * like {@code //ClassDeclaration[@Identifier='Foo']} instead of the longer
     * {@code //ClassDeclaration/SimpleIdentifier/T-Identifier[@Text='Foo']}.
     * Exposed as XPath attribute {@code @Identifier}.
     *
     * <p>Visible in the PMD Designer only on nodes that have a SimpleIdentifier child.
     * Implemented in {@link KotlinInnerNode}; returns {@code null} by default.
     */
    default @Nullable String getIdentifier() {
        return null;
    }
}
