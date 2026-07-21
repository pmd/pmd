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
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser;
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
            KotlinParser.KtModifiers modifiers = findModifiers((KotlinNode) contextNode);
            if (modifiers == null) {
                return Collections.emptyList();
            }
            return collectModifierTexts(modifiers);
        }
    }

    private static List<String> collectModifierTexts(KotlinParser.KtModifiers modifiers) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < modifiers.getNumChildren(); i++) {
            KotlinNode child = modifiers.getChild(i);
            if (child instanceof KotlinParser.KtModifier) {
                String text = getModifierText((KotlinParser.KtModifier) child);
                if (text != null) {
                    result.add(text);
                }
            }
            // KtAnnotation children are skipped
        }
        return result;
    }

    private static KotlinParser.KtModifiers findModifiers(KotlinNode declNode) {
        for (int i = 0; i < declNode.getNumChildren(); i++) {
            KotlinNode child = declNode.getChild(i);
            if (child instanceof KotlinParser.KtModifiers) {
                return (KotlinParser.KtModifiers) child;
            }
        }
        return null;
    }

    /**
     * Returns the modifier keyword text from a {@code KtModifier} node by
     * finding the first terminal leaf in its subtree (the keyword token).
     */
    static String getModifierText(KotlinParser.KtModifier modifier) {
        KotlinTerminalNode terminal = firstTerminal(modifier);
        return terminal != null ? terminal.getText() : null;
    }

    private static KotlinTerminalNode firstTerminal(KotlinNode node) {
        if (node instanceof KotlinTerminalNode) {
            return (KotlinTerminalNode) node;
        }
        for (int i = 0; i < node.getNumChildren(); i++) {
            KotlinTerminalNode found = firstTerminal(node.getChild(i));
            if (found != null) {
                return found;
            }
        }
        return null;
    }
}
