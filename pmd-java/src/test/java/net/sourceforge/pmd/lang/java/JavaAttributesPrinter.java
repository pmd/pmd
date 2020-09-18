/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

/**
 * Special tweak to remove deprecated attributes of AccessNode
 */
public class JavaAttributesPrinter extends RelevantAttributePrinter {

    @Override
    protected void fillAttributes(@NonNull Node node, @NonNull List<AttributeInfo> result) {
        super.fillAttributes(node, result);
        if (node instanceof ASTModifierList) {
            result.add(getModifierAttr("EffectiveModifiers", ((ASTModifierList) node).getEffectiveModifiers()));
            result.add(getModifierAttr("ExplicitModifiers", ((ASTModifierList) node).getExplicitModifiers()));
        }
    }

    @Override
    protected boolean ignoreAttribute(@NonNull Node node, @NonNull Attribute attribute) {
        return super.ignoreAttribute(node, attribute)
            // Deprecated attributes are removed from the output
            // This is only for java-grammar, since deprecated getters will
            // be removed, it would be a pain to update all tree dump tests
            // everytime. OTOH failing dump tests would warn us that we removed
            // something that wasn't deprecated
            || attribute.isDeprecated()
            || attribute.getName().equals("Expression") && node instanceof ASTExpression;
    }

    private AttributeInfo getModifierAttr(String name, Set<JModifier> mods) {
        return new AttributeInfo(name, mods.stream().map(JModifier::getToken).collect(Collectors.joining(", ", "{", "}")));
    }
}
