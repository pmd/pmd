/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinNode;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParsingHelper;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinTerminalNode;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

class KotlinDesignerBindingsTest {

    @Test
    void terminalNodeText() {
        KotlinParser.KtKotlinFile kotlinFile = KotlinParsingHelper.DEFAULT.parse("class foo {}");
        KotlinTerminalNode kotlinTerminalNode = kotlinFile.descendants(KotlinTerminalNode.class).firstOrThrow();
        assertAttribute(kotlinTerminalNode, "Text", "class");
    }

    @Test
    void classIdentifierAttribute() {
        KotlinParser.KtKotlinFile kotlinFile = KotlinParsingHelper.DEFAULT.parse("class foo {}");
        KotlinParser.KtClassDeclaration classDeclaration = kotlinFile.descendants(KotlinParser.KtClassDeclaration.class).firstOrThrow();
        assertAttribute(classDeclaration, "Identifier", "foo");
    }

    @Test
    void companionObjectModifierAttribute() {
        KotlinParser.KtKotlinFile kotlinFile = KotlinParsingHelper.DEFAULT.parse("class foo { public companion object {} }");
        KotlinParser.KtCompanionObject companionObject = kotlinFile.descendants(KotlinParser.KtCompanionObject.class).firstOrThrow();
        assertAttribute(companionObject, "Modifiers", "public");
    }

    private static void assertAttribute(KotlinNode node, String attributeName, String expectedValue) {
        Attribute mainAttribute = KotlinDesignerBindings.INSTANCE.getMainAttribute(node);
        assertNotNull(mainAttribute);
        assertEquals(attributeName, mainAttribute.getName());
        assertEquals(expectedValue, mainAttribute.getStringValue());
    }
}
