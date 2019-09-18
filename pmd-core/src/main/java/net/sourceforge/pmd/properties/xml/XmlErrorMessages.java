/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

/**
 * @author Clément Fournier
 */
public final class XmlErrorMessages {

    public static final String UNEXPECTED_ELEMENT = "Unexpected element '%s', expecting %s";
    public static final String MISSING_REQUIRED_ATTRIBUTE = "Required attribute '%s' is missing";
    public static final String MISSING_REQUIRED_ELEMENT = "Required child element '%s' is missing";
    public static final String IGNORED_DUPLICATE_CHILD_ELEMENT = "Expecting a single '%s' child, this will be ignored";
    public static final String PROPERTY_DOESNT_SUPPORT_VALUE_ATTRIBUTE = "The type %s does not support the attribute syntax.\nUse a nested element, e.g. %s";
    public static final String DEPRECATED_USE_OF_ATTRIBUTE = "The use of the '%s' attribute is deprecated. Use a nested element, e.g. %s";

    private XmlErrorMessages() {
        // utility class
    }
}
