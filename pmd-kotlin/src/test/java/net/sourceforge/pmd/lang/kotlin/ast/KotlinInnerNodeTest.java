/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

class KotlinInnerNodeTest {

    @Test
    void addAttributesSkipsNullValuedAttributes() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val x = 1");
        List<Attribute> result = new ArrayList<>();
        Map<String, Attribute> byName = new HashMap<>();
        Attribute nullValued = new Attribute(file, "Foo", null);
        KotlinInnerNode.addAttributes(
                Arrays.asList(nullValued).iterator(), result, byName);
        assertEquals(0, result.size());
    }

    @Test
    void addAttributesThrowsOnDuplicateNonNullName() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val x = 1");
        List<Attribute> result = new ArrayList<>();
        Map<String, Attribute> byName = new HashMap<>();
        Attribute first = new Attribute(file, "Foo", "bar");
        Attribute duplicate = new Attribute(file, "Foo", "baz");
        assertThrows(IllegalStateException.class, () ->
                KotlinInnerNode.addAttributes(
                        Arrays.asList(first, duplicate).iterator(), result, byName));
    }

    @Test
    void addAttributesAllowsDistinctNames() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val x = 1");
        List<Attribute> result = new ArrayList<>();
        Map<String, Attribute> byName = new HashMap<>();
        Attribute first = new Attribute(file, "Foo", "bar");
        Attribute second = new Attribute(file, "Baz", "qux");
        KotlinInnerNode.addAttributes(
                Arrays.asList(first, second).iterator(), result, byName);
        assertEquals(2, result.size());
    }

    @Test
    void addAttributesAllowsDuplicateStandardPositionAttributeWithSameValue() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val x = 1");
        List<Attribute> result = new ArrayList<>();
        Map<String, Attribute> byName = new HashMap<>();
        Attribute first = new Attribute(file, "BeginLine", "1");
        Attribute duplicate = new Attribute(file, "BeginLine", "1");
        KotlinInnerNode.addAttributes(
                Arrays.asList(first, duplicate).iterator(), result, byName);
        assertEquals(1, result.size());
    }

    @Test
    void addAttributesThrowsOnStandardPositionAttributeWithDifferentValue() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("val x = 1");
        List<Attribute> result = new ArrayList<>();
        Map<String, Attribute> byName = new HashMap<>();
        Attribute first = new Attribute(file, "BeginLine", "1");
        Attribute conflicting = new Attribute(file, "BeginLine", "2");
        assertThrows(IllegalStateException.class, () ->
                KotlinInnerNode.addAttributes(
                        Arrays.asList(first, conflicting).iterator(), result, byName));
    }
}
