/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

/**
 * Special tweak to remove deprecated attributes of AccessNode
 */
public class JavaAttributesPrinter extends RelevantAttributePrinter {


    @Override
    protected @NonNull Iterable<AttributeInfo> getAttributes(@NonNull Node node) {
        if (node instanceof ASTModifierList) {
            List<AttributeInfo> attributes = new ArrayList<>();
            super.getAttributes(node).forEach(attributes::add);

            attributes.add(getModifierAttr("EffectiveModifiers", ((ASTModifierList) node).getEffectiveModifiers()));
            attributes.add(getModifierAttr("ExplicitModifiers", ((ASTModifierList) node).getExplicitModifiers()));

            return attributes;
        }
        return super.getAttributes(node);
    }

    @Override
    protected boolean ignoreAttribute(@NonNull Node node, @NonNull Attribute attribute) {
        return super.ignoreAttribute(node, attribute)
            || attribute.replacementIfDeprecated() != null; // deprecated attribute is removed
    }

    private AttributeInfo getModifierAttr(String name, Set<JModifier> mods) {
        return new AttributeInfo(name, mods.stream().map(JModifier::getToken).collect(Collectors.joining(", ", "{", "}")));
    }
}
