/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.util.CollectionUtil.emptyList;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RulesetFactoryTestBase;
import net.sourceforge.pmd.properties.internal.PropertyParsingUtil;
import net.sourceforge.pmd.util.internal.xml.XmlErrorMessages;

/**
 * @author Clément Fournier
 */
class PropertySyntaxTest extends RulesetFactoryTestBase {

    private PropertyDescriptor<?> defineProperty(String propDef) {
        Rule rule = loadFirstRule(contextForPropertyDef(propDef));

        ArrayList<PropertyDescriptor<?>> descriptors = new ArrayList<>(rule.getPropertyDescriptors());
        descriptors.removeAll(setOf(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR,
                                    Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR));
        return descriptors.get(0);
    }

    private static String contextForPropertyDef(String propDef) {
        return rulesetXml(
            dummyRule(
                properties(
                    propDef
                )
            )
        );
    }


    @Test
    void testPropDefXml() {
        PropertyDescriptor<?> prop = defineProperty(
            propertyDefWithValueAttr("pname", "pdesc", "String", "strvalue")
        );

        assertEquals("pname", prop.name());
        assertEquals("strvalue", prop.defaultValue());
    }

    @Test
    void testNumericPropDefWithoutBounds() {
        // https://github.com/pmd/pmd/issues/1204
        PropertyDescriptor<?> prop = defineProperty(
            "<property name='pname' description='d' type='Integer' value='4' />"
        );

        assertEquals("pname", prop.name());
        assertEquals(4, prop.defaultValue());
        assertEquals(emptyList(), prop.serializer().getConstraints());
    }

    @Test
    void testNumericPropDefWithMinBound() {
        // https://github.com/pmd/pmd/issues/1204
        PropertyDescriptor<?> prop = defineProperty(
            "<property name='pname' description='d' type='Integer' value='4' min='1' />"
        );

        assertEquals("pname", prop.name());
        assertEquals(4, prop.defaultValue());
        assertEquals(1, prop.serializer().getConstraints().size());
        assertEquals("Should be greater or equal to 1", prop.serializer().getConstraints().get(0).getConstraintDescription());
    }


    @Test
    void testNumericPropDefWithMaxBound() {
        // https://github.com/pmd/pmd/issues/1204
        PropertyDescriptor<?> prop = defineProperty(
            "<property name='pname' description='d' type='Integer' value='4' max='6' />"
        );

        assertEquals("Should be smaller or equal to 6", prop.serializer().getConstraints().get(0).getConstraintDescription());
    }

    @Test
    void testNumericPropDefWithMaxAndMin() {
        // https://github.com/pmd/pmd/issues/1204
        PropertyDescriptor<?> prop = defineProperty(
            "<property name='pname' description='d' type='Integer' value='4' max='6' min='2' />"
        );

        assertEquals("Should be between 2 and 6", prop.serializer().getConstraints().get(0).getConstraintDescription());
    }

    @Test
    void testNumericPropDefWithMaxAndMinUnordered() {
        assertCannotParse(
            contextForPropertyDef(
                "<property name='pname' description='d' type='Integer' value='4' max='6' min='12' />"
            )
        );
        verifyFoundAnErrorWithMessage(containing(XmlErrorMessages.ERR__INVALID_VALUE_RANGE));
    }

    @Test
    void testNumericPropConstraintViolated() {
        // https://github.com/pmd/pmd/issues/1204
        assertCannotParse(contextForPropertyDef(
            "<property name='pname' description='d' type='Integer' value='4' max='1' />"
        ));
        verifyFoundAnErrorWithMessage(containing("'4' should be smaller or equal to 1"));

    }


    @Test
    void testStringProp() {
        assertValueRoundTrip(PropertyParsingUtil.STRING, "ad", "ad");
        assertValueRoundTrip(PropertyParsingUtil.STRING, "", "");
    }

    @Test
    void testStringListProp() {
        assertValueRoundTrip(PropertyParsingUtil.STRING_LIST, "ad,j", listOf("ad", "j"));
    }


    private <T> void assertValueRoundTrip(PropertySerializer<T> mapper, String input, T expected) {
        T parsed = mapper.fromString(input);
        assertEquals(expected, parsed, "fromString");
        String str = mapper.toString(parsed);
        assertEquals(input, str, "toString");
    }

}
