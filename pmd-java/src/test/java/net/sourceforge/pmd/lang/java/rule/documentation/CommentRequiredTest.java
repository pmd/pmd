/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.FieldSource;

import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.test.PmdRuleTst;

class CommentRequiredTest extends PmdRuleTst {

    private static final List<List<String>> SKIP_PROPERTY_LISTS = Arrays.asList(
            Arrays.asList(
                    "minimumVisibility",
                    "publicMethodCommentRequirement",
                    "protectedMethodCommentRequirement",
                    "packageMethodCommentRequirement",
                    "enumCommentRequirement",
                    "classCommentRequirement"),
            Arrays.asList(
                    "minimumVisibility",
                    "methodCommentRequirement",
                    "typeCommentRequirement"));

    @ParameterizedTest
    @FieldSource("SKIP_PROPERTY_LISTS")
    void allCommentTypesIgnored(List<String> skippedProperties) {
        CommentRequiredRule rule = new CommentRequiredRule();
        assertNull(rule.dysfunctionReason(), "By default, the rule should be functional");

        List<PropertyDescriptor<?>> propertyDescriptors = getProperties(rule, skippedProperties);
        for (PropertyDescriptor<?> property : propertyDescriptors) {
            setPropertyValue(rule, property, "ignored");
        }

        assertNotNull(rule.dysfunctionReason(), "All properties are ignored, rule should be dysfunctional");

        // now, try out combinations: only one of the properties is required.
        for (PropertyDescriptor<?> property : propertyDescriptors) {
            setPropertyValue(rule, property, "required");
            assertNull(rule.dysfunctionReason(),
                    "The property " + property.name() + " is set to required, the rule should be functional.");
            setPropertyValue(rule, property, "ignored");
        }
    }

    @ParameterizedTest
    @CsvSource({"publicMethodCommentRequirement,methodCommentRequirement",
                "protectedMethodCommentRequirement,methodCommentRequirement",
                "packageMethodCommentRequirement,methodCommentRequirement",
                "classCommentRequirement,typeCommentRequirement",
                "enumCommentRequirement,typeCommentRequirement"})
    void deprecatedPropertiesShouldNotBeSetTogetherWithReplacement(String old, String replacement) {
        CommentRequiredRule rule = new CommentRequiredRule();
        setPropertyValue(rule, rule.getPropertyDescriptor(old), "ignored");
        setPropertyValue(rule, rule.getPropertyDescriptor(replacement), "ignored");
        assertTrue(Objects.requireNonNull(rule.dysfunctionReason()).contains("should not be set together with"),
                "Unexpected reason" + rule.dysfunctionReason());
    }

    private static List<PropertyDescriptor<?>> getProperties(Rule rule, List<String> skippedProperties) {
        List<PropertyDescriptor<?>> result = new ArrayList<>();
        for (PropertyDescriptor<?> property : rule.getPropertyDescriptors()) {
            if (property != Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR
                    && property != Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR
                    && !skippedProperties.contains(property.name())) {
                result.add(property);
            }
        }
        return result;
    }

    private static <T> void setPropertyValue(Rule rule, PropertyDescriptor<T> property, String value) {
        rule.setProperty(property, property.serializer().fromString(value));
    }
}
