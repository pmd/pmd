/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.stream.Collectors;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * @since 7.25.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public interface HasModifiers extends KotlinNode {
    KotlinNode getNode();

    /**
     * Returns the explicit modifier keywords of this declaration node as a
     * space-separated string (e.g. {@code "override suspend"}), or {@code null}
     * if this node has no modifier keywords. Annotations inside the modifier list
     * are excluded. Exposed as XPath attribute {@code @Modifiers}.
     */
    @Override
    default String getModifiers() {
        KotlinParser.KtModifiers mods = getNode().firstChild(KotlinParser.KtModifiers.class);
        if (mods == null) {
            return null;
        }

        // KtAnnotation children are not considered
        String result = mods.children(KotlinParser.KtModifier.class)
                .descendants(KotlinTerminalNode.class)
                .toStream()
                .map(KotlinTerminalNode::getText)
                .collect(Collectors.joining(" "));
        return !result.isEmpty() ? result : null;
    }
}
