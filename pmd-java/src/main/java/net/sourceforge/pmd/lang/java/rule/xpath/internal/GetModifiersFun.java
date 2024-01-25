/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * The two functions {@code modifiers} and {@code explicitModifiers}.
 */
public final class GetModifiersFun extends BaseJavaXPathFunction {

    private final boolean explicit;

    public static final GetModifiersFun GET_EFFECTIVE = new GetModifiersFun("modifiers", false);
    public static final GetModifiersFun GET_EXPLICIT = new GetModifiersFun("explicitModifiers", true);

    private GetModifiersFun(String localName, boolean explicit) {
        super(localName);
        this.explicit = explicit;
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
        return (contextNode, arguments) -> {
            if (contextNode instanceof AccessNode) {
                ASTModifierList modList = ((AccessNode) contextNode).getModifiers();
                Set<JModifier> mods = explicit ? modList.getExplicitModifiers()
                                               : modList.getEffectiveModifiers();
                return CollectionUtil.map(mods, JModifier::getToken).toArray(new String[0]);
            }
            return null;
        };
    }
}
