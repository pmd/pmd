/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml.internal;

public final class XmlErrorMessages {

    private static final String THIS_WILL_BE_IGNORED = ", this will be ignored";

    public static final String ERR__UNEXPECTED_ELEMENT = "Unexpected element ''{0}'', expecting {1}";
    public static final String ERR__MISSING_REQUIRED_ATTRIBUTE = "Required attribute ''{0}'' is missing";
    public static final String ERR__MISSING_REQUIRED_ELEMENT = "Required child element named {0} is missing";

    public static final String IGNORED__UNEXPECTED_ELEMENT = ERR__UNEXPECTED_ELEMENT + THIS_WILL_BE_IGNORED;
    public static final String IGNORED__DUPLICATE_CHILD_ELEMENT = "Duplicated child with name ''{0}''" + THIS_WILL_BE_IGNORED;
    public static final String IGNORED__DUPLICATE_PROPERTY_SETTER = "Duplicate property tag with name ''{0}''" + THIS_WILL_BE_IGNORED;

    public static final String ERR__UNSUPPORTED_VALUE_ATTRIBUTE = "This property does not support the attribute syntax.\nUse a nested element, e.g. {1}";
    public static final String ERR__PROPERTY_DOES_NOT_EXIST = "Cannot set non-existent property ''{0}'' on rule {1}";
    public static final String ERR__CONSTRAINT_NOT_SATISFIED = "Property constraint(s) not satisfied: {0}";
    public static final String ERR__LIST_CONSTRAINT_NOT_SATISFIED = "Property constraint(s) not satisfied on items";

    public static final String WARN__DEPRECATED_USE_OF_ATTRIBUTE = "The use of the ''{0}'' attribute is deprecated. Use a nested element, e.g. {1}";

    private XmlErrorMessages() {
        // utility class
    }
}
