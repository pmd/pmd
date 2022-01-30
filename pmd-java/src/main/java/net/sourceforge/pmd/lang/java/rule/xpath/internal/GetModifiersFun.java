/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.xpath.internal;

import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.rule.xpath.internal.AstElementNode;
import net.sourceforge.pmd.util.CollectionUtil;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.SequenceExtent;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

/**
 * The two functions {@code modifiers} and {@code explicitModifiers}.
 */
public final class GetModifiersFun extends BaseJavaXPathFunction {

    private static final SequenceType[] ARGTYPES = {};
    private final boolean explicit;

    public static final GetModifiersFun GET_EFFECTIVE = new GetModifiersFun("modifiers", false);
    public static final GetModifiersFun GET_EXPLICIT = new GetModifiersFun("explicitModifiers", true);

    private GetModifiersFun(String localName, boolean explicit) {
        super(localName);
        this.explicit = explicit;
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return ARGTYPES;
    }

    @Override
    public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
        return SequenceType.STRING_SEQUENCE;
    }

    @Override
    public boolean dependsOnFocus() {
        return true;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @Override
            public Sequence call(XPathContext context, Sequence[] arguments) {
                Node contextNode = ((AstElementNode) context.getContextItem()).getUnderlyingNode();

                if (contextNode instanceof AccessNode) {
                    ASTModifierList modList = ((AccessNode) contextNode).getModifiers();
                    Set<JModifier> mods = explicit ? modList.getExplicitModifiers()
                                                   : modList.getEffectiveModifiers();
                    return new SequenceExtent(CollectionUtil.map(mods, mod -> new StringValue(mod.getToken())));
                } else {
                    return EmptySequence.getInstance();
                }
            }
        };
    }
}
