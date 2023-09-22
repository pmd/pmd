/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.internal.xml;

// CHECKSTYLE:OFF
public final class XmlErrorMessages {

    public static final String ERR__INVALID_VALUE_RANGE = "Minimum value should be lower than maximum value";
    private static final String THIS_WILL_BE_IGNORED = ", this will be ignored";

    /** {0}: unexpected element name; {1}: parent node name; {2}: list of allowed elements in this context */
    public static final String ERR__UNEXPECTED_ELEMENT = "Unexpected element ''{0}'' in {1}, expecting {2}";
    /** {0}: unexpected element name; {1}: parent node name */
    public static final String ERR__UNEXPECTED_ELEMENT_IN = "Unexpected element ''{0}'' in {1}";
    /** {0}: unexpected attr name; {1}: parent node name */
    public static final String ERR__UNEXPECTED_ATTRIBUTE_IN = "Unexpected attribute ''{0}'' in {1}";
    public static final String ERR__MISSING_REQUIRED_ATTRIBUTE = "Required attribute ''{0}'' is missing";
    public static final String ERR__BLANK_REQUIRED_ATTRIBUTE = "Required attribute ''{0}'' is blank";
    public static final String ERR__MISSING_REQUIRED_ELEMENT = "Required child element named ''{0}'' is missing";

    /** {0}: unexpected element name; {1}: parent node name; {2}: allowed elements in this context */
    public static final String IGNORED__UNEXPECTED_ELEMENT = ERR__UNEXPECTED_ELEMENT + THIS_WILL_BE_IGNORED;
    /** {0}: unexpected element name; {1}: parent node name */
    public static final String IGNORED__UNEXPECTED_ELEMENT_IN = ERR__UNEXPECTED_ELEMENT_IN + THIS_WILL_BE_IGNORED;
    public static final String IGNORED__UNEXPECTED_ATTRIBUTE_IN = ERR__UNEXPECTED_ATTRIBUTE_IN + THIS_WILL_BE_IGNORED;
    public static final String IGNORED__DUPLICATE_CHILD_ELEMENT = "Duplicated child with name ''{0}''" + THIS_WILL_BE_IGNORED;
    public static final String IGNORED__DUPLICATE_PROPERTY_SETTER = "Duplicate property tag with name ''{0}''" + THIS_WILL_BE_IGNORED;
    public static final String IGNORED__PROPERTY_CHILD_HAS_PRECEDENCE = "Both a ''value'' attribute and a child element are present, the attribute will be ignored";

    public static final String ERR__UNSUPPORTED_VALUE_ATTRIBUTE = "This property does not support the attribute syntax.\nUse a nested element, e.g. {1}";
    public static final String ERR__PROPERTY_DOES_NOT_EXIST = "Cannot set non-existent property ''{0}'' on rule {1}";
    public static final String ERR__CONSTRAINT_NOT_SATISFIED = "Property constraint(s) not satisfied: {0}";
    public static final String ERR__LIST_CONSTRAINT_NOT_SATISFIED = "Property constraint(s) not satisfied on items";
    public static final String ERR__INVALID_VERSION_RANGE = "Invalid language version range, minimum version ''{0}'' is greater than maximum version ''{1}''";
    public static final String ERR__INVALID_LANG_VERSION_NO_NAMED_VERSION = "Invalid language version ''{0}'' for language ''{1}'', the language has no named versions";
    public static final String ERR__INVALID_LANG_VERSION = "Invalid language version ''{0}'' for language ''{1}'', supported versions are {2}";

    public static final String WARN__DEPRECATED_USE_OF_ATTRIBUTE = "The use of the ''{0}'' attribute is deprecated. Use a nested element, e.g. {1}";
    public static final String ERR__INVALID_PRIORITY_VALUE = "Not a valid priority: ''{0}'', expected a number in [1,5]";
    public static final String ERR__UNSUPPORTED_PROPERTY_TYPE = "Unsupported property type ''{0}''";
    public static final String WARN__DELIMITER_DEPRECATED = "Delimiter attribute is not supported anymore, values are always comma-separated.";

    private XmlErrorMessages() {
        // utility class
    }
}
