/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute;

/**
 * @since 7.25.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public interface HasModifiers extends KotlinNode {
    KotlinNode getNode();

    /**
     * Returns the explicit modifier keywords of this declaration node, in source order
     * (e.g. {@code ["override", "suspend"]}), or an empty list if this node has no
     * modifier keywords. Annotations inside the modifier list are excluded.
     *
     * <p>Java-rule API only: not exposed as an XPath attribute (space-separated
     * strings are fragile to match reliably). Use {@code pmd-kotlin:modifiers()}
     * from XPath instead.
     */
    @NoAttribute
    default List<String> getModifiers() {
        KotlinParser.KtModifiers mods = getNode().firstChild(KotlinParser.KtModifiers.class);
        if (mods == null) {
            return Collections.emptyList();
        }

        // KtAnnotation children are not considered
        List<String> result = new ArrayList<>();
        for (KotlinParser.KtModifier modifier : mods.children(KotlinParser.KtModifier.class)) {
            KotlinTerminalNode terminal = modifier.descendants(KotlinTerminalNode.class).first();
            if (terminal != null) {
                result.add(terminal.getText());
            }
        }
        return result;
    }
}
