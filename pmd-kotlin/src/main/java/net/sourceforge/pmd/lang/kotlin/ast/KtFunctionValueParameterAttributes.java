/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * @since 7.27.0
 * @experimental See {@link AttributeView}.
 */
@Experimental
public class KtFunctionValueParameterAttributes extends AttributeView<KotlinParser.KtFunctionValueParameter> implements HasTypeName {
    public KtFunctionValueParameterAttributes(KotlinParser.KtFunctionValueParameter node) {
        super(node);
    }

    /**
     * Returns the parameter modifier keywords ({@code vararg}, {@code noinline},
     * {@code crossinline}) as a space-separated string, or {@code null} if none.
     *
     * <p>Note: FunctionValueParameter uses {@code parameterModifiers} in the grammar
     * (not {@code modifiers}), so this is a custom implementation rather than
     * using the {@link HasModifiers} interface.
     */
    public @Nullable String getModifiers() {
        KotlinParser.KtParameterModifiers mods = node.firstChild(KotlinParser.KtParameterModifiers.class);
        if (mods == null) {
            return null;
        }
        String result = mods.children(KotlinParser.KtParameterModifier.class)
                .descendants(KotlinTerminalNode.class)
                .toStream()
                .map(KotlinTerminalNode::getText)
                .collect(Collectors.joining(" "));
        return !result.isEmpty() ? result : null;
    }
}
