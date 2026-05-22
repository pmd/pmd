/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.internal;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.HasModifiers;
import net.sourceforge.pmd.lang.kotlin.ast.HasSimpleIdentifier;
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
        if (node instanceof HasSimpleIdentifier) {
            String id = ((HasSimpleIdentifier) node).getIdentifier();
            if (id != null) {
                return new Attribute(node, "Identifier", id);
            }
        }
        if (node instanceof HasModifiers) {
            String mods = ((HasModifiers) node).getModifiers();
            if (mods != null) {
                return new Attribute(node, "Modifiers", mods);
            }
        }
        return super.getMainAttribute(node);
    }
}
