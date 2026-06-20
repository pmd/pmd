/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParsingHelper;

class KotlinNodeTypeDataTest {

    private static final KotlinParsingHelper PARSER = KotlinParsingHelper.DEFAULT;

    private KtKotlinFile parse(String source) {
        return PARSER.parse(source);
    }

    // --- typeName ---

    @Test
    void typeNameNullWhenNotSet() {
        KtKotlinFile root = parse("val x = 1");
        assertNull(KotlinNodeTypeData.getTypeName(root));
    }

    @Test
    void typeNameRoundtrip() {
        KtKotlinFile root = parse("val x = 1");
        KotlinNodeTypeData.setTypeName(root, "java.lang.String");
        assertEquals("java.lang.String", KotlinNodeTypeData.getTypeName(root));
    }

    // --- returnTypeName ---

    @Test
    void returnTypeNameNullWhenNotSet() {
        KtKotlinFile root = parse("fun foo() {}");
        assertNull(KotlinNodeTypeData.getReturnTypeName(root));
    }

    @Test
    void returnTypeNameRoundtrip() {
        KtKotlinFile root = parse("fun foo() {}");
        KotlinNodeTypeData.setReturnTypeName(root, "kotlin.Int");
        assertEquals("kotlin.Int", KotlinNodeTypeData.getReturnTypeName(root));
    }

    // --- annotationFqNames ---

    @Test
    void annotationFqNamesEmptyWhenNotSet() {
        KtKotlinFile root = parse("val x = 1");
        assertTrue(KotlinNodeTypeData.getAnnotationFqNames(root).isEmpty());
    }

    @Test
    void annotationFqNamesRoundtrip() {
        KtKotlinFile root = parse("val x = 1");
        KotlinNodeTypeData.setAnnotationFqNames(root, "org.springframework.stereotype.Service,kotlin.Deprecated");
        List<String> names = KotlinNodeTypeData.getAnnotationFqNames(root);
        assertEquals(2, names.size());
        assertTrue(names.contains("org.springframework.stereotype.Service"));
        assertTrue(names.contains("kotlin.Deprecated"));
    }

    // --- typeInfoAvailable ---

    @Test
    void typeInfoAvailableTrueAfterSet() {
        KtKotlinFile root = parse("val x = 1");
        KotlinNodeTypeData.setTypeInfoAvailable(root);
        assertTrue(KotlinNodeTypeData.isTypeInfoAvailable(root));
    }

    // --- InternalApiBridge (public setters) ---

    @Test
    void internalApiBridgeSetTypeName() {
        KtKotlinFile root = parse("val x = 1");
        InternalApiBridge.setTypeName(root, "java.util.List");
        assertEquals("java.util.List", KotlinNodeTypeData.getTypeName(root));
    }

    @Test
    void internalApiBridgeSetTypeInfoAvailable() {
        KtKotlinFile root = parse("val x = 1");
        InternalApiBridge.setTypeInfoAvailable(root);
        assertTrue(KotlinNodeTypeData.isTypeInfoAvailable(root));
    }
}
