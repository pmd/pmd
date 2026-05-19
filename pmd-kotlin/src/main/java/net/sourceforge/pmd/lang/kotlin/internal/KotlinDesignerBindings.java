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
import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTerminalNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.util.designerbindings.DesignerBindings.DefaultDesignerBindings;

/**
 * Designer bindings for Kotlin. Populates the "Additional info" section
 * of the PMD Designer with resolved type names and modifiers -- mirroring
 * what {@code JavaDesignerBindings} does for Java nodes.
 */
public final class KotlinDesignerBindings extends DefaultDesignerBindings {

    public static final KotlinDesignerBindings INSTANCE = new KotlinDesignerBindings();

    private KotlinDesignerBindings() {
    }

    /**
     * Returns the "main" attribute shown inline next to the node name in the Designer tree.
     * <ul>
     *   <li>T- terminal nodes: {@code @Text} -- the token text.</li>
     *   <li>Inner nodes with {@code @TypeName} set: shows the resolved type name.</li>
     *   <li>Inner nodes with {@code @ReturnTypeName} set (e.g. {@code FunctionDeclaration}):
     *       shows the return type name.</li>
     *   <li>Inner nodes with {@code @Identifier} set: shows the identifier name.</li>
     *   <li>Otherwise: falls back to the default (image-based) behaviour.</li>
     * </ul>
     */
    @Override
    public Attribute getMainAttribute(Node node) {
        if (node instanceof KotlinTerminalNode) {
            String text = ((KotlinTerminalNode) node).getText();
            if (text != null && !text.isEmpty()) {
                return new Attribute(node, "Text", text);
            }
        }
        if (node instanceof KotlinNode) {
            KotlinNode kNode = (KotlinNode) node;
            String typeName = kNode.getTypeName();
            if (typeName != null) {
                return new Attribute(node, "TypeName", typeName);
            }
            String returnTypeName = kNode.getReturnTypeName();
            if (returnTypeName != null) {
                return new Attribute(node, "ReturnTypeName", returnTypeName);
            }
            String identifier = kNode.getIdentifier();
            if (identifier != null) {
                return new Attribute(node, "Identifier", identifier);
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
        KotlinNode kNode = (KotlinNode) node;

        // Modifiers -- shown as "pmd-kotlin:modifiers(): (override, suspend)"
        String mods = kNode.getModifiers();
        if (mods != null) {
            String formatted = Arrays.stream(mods.split(" "))
                    .collect(Collectors.joining(", ", "(", ")"));
            info.add(new AdditionalInfo("pmd-kotlin:modifiers(): " + formatted));
        }

        // TypeName -- shown as "TypeName: org.example.Service"
        String typeName = kNode.getTypeName();
        if (typeName != null) {
            info.add(new AdditionalInfo("TypeName: " + typeName));
        }

        // ReturnTypeName -- shown as "ReturnTypeName: kotlin.String"
        String returnTypeName = kNode.getReturnTypeName();
        if (returnTypeName != null) {
            info.add(new AdditionalInfo("ReturnTypeName: " + returnTypeName));
        }

        return info;
    }
}
