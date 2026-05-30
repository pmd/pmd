/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.kotlin.ast.AttributeView;
import net.sourceforge.pmd.lang.kotlin.ast.HasModifiers;
import net.sourceforge.pmd.lang.kotlin.ast.HasSimpleIdentifier;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTerminalNode;
import net.sourceforge.pmd.lang.kotlin.types.KotlinTypeMapper;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings.DefaultDesignerBindings;

/**
 * Designer bindings for Kotlin. Populates the "Additional info" section
 * of the PMD Designer with resolved type names and modifiers, while also showing
 * a useful main attribute next to each node in the designer tree.
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

        if (node instanceof KotlinNode) {
            KotlinNode kotlinNode = (KotlinNode) node;
            AttributeView<?> attributeView = AttributeView.create(kotlinNode);
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

            String typeName = KotlinTypeMapper.getTypeName(kotlinNode);
            if (typeName != null) {
                return new Attribute(node, "TypeName", typeName);
            }
            String returnTypeName = KotlinTypeMapper.getReturnTypeName(kotlinNode);
            if (returnTypeName != null) {
                return new Attribute(node, "ReturnTypeName", returnTypeName);
            }
        }
        return super.getMainAttribute(node);
    }

    @Override
    public Collection<AdditionalInfo> getAdditionalInfo(Node node) {
        List<AdditionalInfo> info = new ArrayList<>(super.getAdditionalInfo(node));
        if (!(node instanceof KotlinNode)) {
            return info;
        }
        KotlinNode kotlinNode = (KotlinNode) node;
        AttributeView<?> attributeView = AttributeView.create(kotlinNode);

        String mods = attributeView instanceof HasModifiers
                ? ((HasModifiers) attributeView).getModifiers()
                : null;
        if (mods != null) {
            String formatted = Arrays.stream(mods.split(" "))
                    .collect(Collectors.joining(", ", "(", ")"));
            info.add(new AdditionalInfo("pmd-kotlin:modifiers(): " + formatted));
        }

        String typeName = KotlinTypeMapper.getTypeName(kotlinNode);
        if (typeName != null) {
            info.add(new AdditionalInfo("TypeName: " + typeName));
        }

        String returnTypeName = KotlinTypeMapper.getReturnTypeName(kotlinNode);
        if (returnTypeName != null) {
            info.add(new AdditionalInfo("ReturnTypeName: " + returnTypeName));
        }

        return info;
    }
}
