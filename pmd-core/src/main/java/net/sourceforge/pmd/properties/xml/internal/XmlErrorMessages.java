/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml.internal;

public final class XmlErrorMessages {

    public static final String UNEXPECTED_ELEMENT = "Unexpected element '{0}', expecting {0}";
    public static final String MISSING_REQUIRED_ATTRIBUTE = "Required attribute '{0}' is missing";
    public static final String MISSING_REQUIRED_ELEMENT = "Required child element '{0}' is missing";
    public static final String MISSING_REQUIRED_ELEMENT_EITHER = "Required child element named {0} is missing";
    public static final String IGNORED_DUPLICATE_CHILD_ELEMENT = "Expecting a single '{0}' child, this will be ignored";
    public static final String PROPERTY_DOESNT_SUPPORT_VALUE_ATTRIBUTE = "The type {0} does not support the attribute syntax.\nUse a nested element, e.g. {1}";
    public static final String DEPRECATED_USE_OF_ATTRIBUTE = "The use of the '{0}' attribute is deprecated. Use a nested element, e.g. {1}";
    public static final String CONSTRAINT_NOT_SATISFIED = "Property constraint(s) not satisfied: {0}";
    public static final String LIST_CONSTRAINT_NOT_SATISFIED = "Property constraint(s) not satisfied on items";

    private XmlErrorMessages() {
        // utility class
    }
}
