/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtModifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtModifiers;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTerminalNode;
import net.sourceforge.pmd.lang.rule.xpath.impl.XPathFunctionException;

/**
 * XPath function {@code pmd-kotlin:modifiers()}.
 *
 * <p>Returns the explicit modifiers of the context declaration node as a sequence
 * of lowercase strings. Applicable to {@code ClassDeclaration},
 * {@code FunctionDeclaration}, {@code PropertyDeclaration},
 * {@code ObjectDeclaration}, and similar declaration nodes that carry a
 * {@code Modifiers} child.
 *
 * <p>Supported modifier values:
 * <ul>
 *   <li>Visibility: {@code public}, {@code private}, {@code protected}, {@code internal}</li>
 *   <li>Inheritance: {@code abstract}, {@code final}, {@code open}</li>
 *   <li>Class: {@code data}, {@code sealed}, {@code enum}, {@code inner},
 *       {@code value}, {@code annotation}</li>
 *   <li>Member: {@code override}, {@code lateinit}</li>
 *   <li>Function: {@code suspend}, {@code inline}, {@code infix},
 *       {@code operator}, {@code tailrec}, {@code external}</li>
 *   <li>Property: {@code const}</li>
 *   <li>Parameter: {@code vararg}, {@code noinline}, {@code crossinline}</li>
 *   <li>Platform: {@code expect}, {@code actual}</li>
 * </ul>
 *
 * <p>Annotations in the modifier list are not included in the result.
 *
 * <p>Example XPath:
 * <pre>{@code
 * //FunctionDeclaration[pmd-kotlin:modifiers() = 'suspend']
 * //PropertyDeclaration[pmd-kotlin:modifiers() = ('const', 'internal')]
 * //ClassDeclaration[pmd-kotlin:modifiers() = 'data']
 * //FunctionDeclaration[pmd-kotlin:modifiers() = ('override', 'suspend')]
 * }</pre>
 *
 * @since 7.27.0
 */
public final class KotlinModifiersFunction extends BaseKotlinXPathFunction {

    public static final KotlinModifiersFunction INSTANCE = new KotlinModifiersFunction();

    private KotlinModifiersFunction() {
        super("modifiers");
    }

    @Override
    public Type[] getArgumentTypes() {
        return new Type[0];
    }

    @Override
    public Type getResultType() {
        return Type.STRING_SEQUENCE;
    }

    @Override
    public boolean dependsOnContext() {
        return true;
    }

    @Override
    public FunctionCall makeCallExpression() {
        return new ModifiersFunctionCall();
    }

    private static final class ModifiersFunctionCall implements FunctionCall {
        @Override
        public Object call(@Nullable Node contextNode, Object[] arguments) throws XPathFunctionException {
            if (!(contextNode instanceof KotlinNode)) {
                return Collections.emptyList();
            }
            KtModifiers modifiers = ((KotlinNode) contextNode).firstChild(KtModifiers.class);
            if (modifiers == null) {
                return Collections.emptyList();
            }
            return collectModifierTexts(modifiers);
        }
    }

    private static List<String> collectModifierTexts(KtModifiers modifiers) {
        List<String> result = new ArrayList<>();
        // KtAnnotation children are skipped: only KtModifier keywords are collected.
        for (KtModifier modifier : modifiers.children(KtModifier.class)) {
            String text = getModifierText(modifier);
            if (text != null) {
                result.add(text);
            }
        }
        return result;
    }

    /**
     * Returns the modifier keyword text from a {@code KtModifier} node by
     * finding the first terminal leaf in its subtree (the keyword token).
     */
    static String getModifierText(KtModifier modifier) {
        KotlinTerminalNode terminal = modifier.descendants(KotlinTerminalNode.class).first();
        return terminal != null ? terminal.getText() : null;
    }
}
