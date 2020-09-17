/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.documentation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.testframework.PmdRuleTst;

public class CommentRequiredTest extends PmdRuleTst {
    @Test
    public void allCommentTypesIgnored() {
        CommentRequiredRule rule = new CommentRequiredRule();
        assertNull("By default, the rule should be functional", rule.dysfunctionReason());

        List<PropertyDescriptor<?>> propertyDescriptors = getProperties(rule);
        // remove  deprecated properties
        for (Iterator<PropertyDescriptor<?>> it = propertyDescriptors.iterator(); it.hasNext();) {
            PropertyDescriptor<?> property = it.next();
            if (property.description().startsWith("Deprecated!")) {
                it.remove();
            }
        }

        for (PropertyDescriptor<?> property : propertyDescriptors) {
            setPropertyValue(rule, property, "Ignored");
        }

        assertNotNull("All properties are ignored, rule should be dysfunctional", rule.dysfunctionReason());

        // now, try out combinations: only one of the properties is required.
        for (PropertyDescriptor<?> property : propertyDescriptors) {
            setPropertyValue(rule, property, "Required");
            assertNull("The property " + property.name() + " is set to required, the rule should be functional.",
                rule.dysfunctionReason());
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
