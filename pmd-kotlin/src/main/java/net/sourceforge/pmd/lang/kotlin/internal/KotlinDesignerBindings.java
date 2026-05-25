/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.internal;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.AttributeView;
import net.sourceforge.pmd.lang.kotlin.ast.HasModifiers;
import net.sourceforge.pmd.lang.kotlin.ast.HasSimpleIdentifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTerminalNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings.DefaultDesignerBindings;

/**
 * Designer bindings for Kotlin. Shows a useful "main attribute" next to each
 * node name in the PMD Designer tree.
 *
 * @since 7.25.0
 */
public final class KotlinDesignerBindings extends DefaultDesignerBindings {

    public static final KotlinDesignerBindings INSTANCE = new KotlinDesignerBindings();

    private KotlinDesignerBindings() {
    }

    @Override
    public Attribute getMainAttribute(Node node) {
        if (node instanceof KotlinTerminalNode) {
            String text = ((KotlinTerminalNode) node).getText();
            if (text != null && !text.isEmpty()) {
                return new Attribute(node, "Text", text);
            }
        }

        AttributeView<?> attributeView = AttributeView.create((KotlinNode) node);
        if (attributeView instanceof HasSimpleIdentifier) {
            String id = ((HasSimpleIdentifier) attributeView).getIdentifier();
            if (id != null) {
                return new Attribute(node, "Identifier", id);
            }
        }
        if (attributeView instanceof HasModifiers) {
            String mods = ((HasModifiers) attributeView).getModifiers();
            if (mods != null) {
                return new Attribute(node, "Modifiers", mods);
            }
        }
        return super.getMainAttribute(node);
    }
}
