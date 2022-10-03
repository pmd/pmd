/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.testframework.PmdRuleTst;

class CommentRequiredTest extends PmdRuleTst {
    @Test
    void allCommentTypesIgnored() {
        CommentRequiredRule rule = new CommentRequiredRule();
        assertNull(rule.dysfunctionReason(), "By default, the rule should be functional");

        List<PropertyDescriptor<?>> propertyDescriptors = getProperties(rule);
        // remove  deprecated properties
        propertyDescriptors.removeIf(property -> property.description().startsWith("Deprecated!"));

        for (PropertyDescriptor<?> property : propertyDescriptors) {
            setPropertyValue(rule, property, "Ignored");
        }

        assertNotNull(rule.dysfunctionReason(), "All properties are ignored, rule should be dysfunctional");

        // now, try out combinations: only one of the properties is required.
        for (PropertyDescriptor<?> property : propertyDescriptors) {
            setPropertyValue(rule, property, "Required");
            assertNull(rule.dysfunctionReason(),
                    "The property " + property.name() + " is set to required, the rule should be functional.");
            setPropertyValue(rule, property, "Ignored");
        }
    }

    private static List<PropertyDescriptor<?>> getProperties(Rule rule) {
        List<PropertyDescriptor<?>> result = new ArrayList<>();
        for (PropertyDescriptor<?> property : rule.getPropertyDescriptors()) {
            if (property != Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR
                    && property != Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR) {
                result.add(property);
            }
        }
        return result;
    }

    private static <T> void setPropertyValue(Rule rule, PropertyDescriptor<T> property, String value) {
        rule.setProperty(property, property.valueFrom(value));
    }
}
