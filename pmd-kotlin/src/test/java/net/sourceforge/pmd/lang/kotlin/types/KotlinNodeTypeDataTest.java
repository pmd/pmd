/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParsingHelper;

class KotlinNodeTypeDataTest {

    private static final KotlinParsingHelper PARSER = KotlinParsingHelper.DEFAULT;

    private KtKotlinFile parse(String source) {
        return PARSER.parse(source);
    }

    // --- type ---

    @Test
    void typeNullWhenNotSet() {
        KtKotlinFile root = parse("val x = 1");
        assertNull(KotlinNodeTypeData.getType(root));
    }

    @Test
    void typeRoundtrip() {
        KtKotlinFile root = parse("val x = 1");
        KotlinNodeTypeData.setType(root, KotlinTypeName.ofFqName("java.lang.String"));
        KotlinTypeName type = KotlinNodeTypeData.getType(root);
        assertEquals("java.lang.String", type.getFqName());
        assertEquals("java.lang.String", type.toDisplayString());
    }

    // --- returnType ---

    @Test
    void returnTypeNullWhenNotSet() {
        KtKotlinFile root = parse("fun foo() {}");
        assertNull(KotlinNodeTypeData.getReturnType(root));
    }

    @Test
    void returnTypeRoundtrip() {
        KtKotlinFile root = parse("fun foo() {}");
        KotlinNodeTypeData.setReturnType(root, KotlinTypeName.ofFqName("kotlin.Int"));
        KotlinTypeName type = KotlinNodeTypeData.getReturnType(root);
        assertEquals("kotlin.Int", type.getFqName());
        assertEquals("kotlin.Int", type.toDisplayString());
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
        KotlinNodeTypeData.setAnnotationFqNames(root, Arrays.asList("org.springframework.stereotype.Service", "kotlin.Deprecated"));
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
    void internalApiBridgeSetType() {
        KtKotlinFile root = parse("val x = 1");
        InternalApiBridge.setType(root, KotlinTypeName.ofFqName("java.util.List"));
        KotlinTypeName type = KotlinNodeTypeData.getType(root);
        assertEquals("java.util.List", type.getFqName());
    }

    @Test
    void internalApiBridgeSetTypeInfoAvailable() {
        KtKotlinFile root = parse("val x = 1");
        InternalApiBridge.setTypeInfoAvailable(root);
        assertTrue(KotlinNodeTypeData.isTypeInfoAvailable(root));
    }
}
