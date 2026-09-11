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
 * @since 7.28.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public class KtFunctionValueParameterAttributes extends AttributeView<KotlinParser.KtFunctionValueParameter> implements HasTypeName {
    public KtFunctionValueParameterAttributes(KotlinParser.KtFunctionValueParameter node) {
        super(node);
    }

    /**
     * Returns the parameter modifier keywords ({@code vararg}, {@code noinline},
     * {@code crossinline}), in source order, or an empty list if none.
     *
     * <p>Note: FunctionValueParameter uses {@code parameterModifiers} in the grammar
     * (not {@code modifiers}), so this is a custom implementation rather than
     * using the {@link HasModifiers} interface.
     *
     * <p>Java-rule API only: not exposed as an XPath attribute (space-separated
     * strings are fragile to match reliably). Use {@code pmd-kotlin:modifiers()}
     * from XPath instead.
     */
    @NoAttribute
    public List<String> getModifiers() {
        KotlinParser.KtParameterModifiers mods = node.firstChild(KotlinParser.KtParameterModifiers.class);
        if (mods == null) {
            return Collections.emptyList();
        }
        List<String> result = new ArrayList<>();
        for (KotlinParser.KtParameterModifier modifier : mods.children(KotlinParser.KtParameterModifier.class)) {
            KotlinTerminalNode terminal = modifier.descendants(KotlinTerminalNode.class).first();
            if (terminal != null) {
                result.add(terminal.getText());
            }
        }
        return result;
    }
}
