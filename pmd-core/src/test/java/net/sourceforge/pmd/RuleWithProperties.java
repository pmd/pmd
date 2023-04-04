/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.List;

import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * Sample rule that detect any node with an image of "Foo", similar to
 * {@link FooRule}. It additionally has some properties in order to test the
 * renderers. Used for testing.
 */
public class RuleWithProperties extends FooRule {

    public static final PropertyDescriptor<String> STRING_PROPERTY_DESCRIPTOR =
        PropertyFactory.stringProperty("stringProperty")
                       .desc("simple string property")
                       .defaultValue("")
                       .build();

    public static final PropertyDescriptor<List<String>> MULTI_STRING_PROPERTY_DESCRIPTOR =
        PropertyFactory.stringListProperty("multiString")
                       .desc("multi string property")
                       .defaultValues("default1", "default2")
                       .delim(',')
                       .build();

    public RuleWithProperties() {
        definePropertyDescriptor(STRING_PROPERTY_DESCRIPTOR);
        definePropertyDescriptor(MULTI_STRING_PROPERTY_DESCRIPTOR);
    }
}
