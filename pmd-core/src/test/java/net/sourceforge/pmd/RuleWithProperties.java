/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.properties.StringMultiProperty;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * Sample rule that detect any node with an image of "Foo", similar to
 * {@link FooRule}. It additionally has some properties in order to test the
 * renderers. Used for testing.
 */
public class RuleWithProperties extends FooRule {
    public static final StringProperty STRING_PROPERTY_DESCRIPTOR = new StringProperty("stringProperty",
            "simple string property", null, 1.0f);
    public static final StringMultiProperty MULTI_STRING_PROPERTY_DESCRIPTOR = new StringMultiProperty("multiString",
            "multi string property", new String[] { "default1", "default2" }, 2.0f, ',');

    public RuleWithProperties() {
        definePropertyDescriptor(STRING_PROPERTY_DESCRIPTOR);
        definePropertyDescriptor(MULTI_STRING_PROPERTY_DESCRIPTOR);
    }
}
