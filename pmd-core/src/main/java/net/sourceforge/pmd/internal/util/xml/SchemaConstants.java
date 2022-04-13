/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util.xml;


/**
 * Constants of the ruleset schema.
 */
public final class SchemaConstants {

    public static final SchemaConstant PROPERTY_TYPE = new SchemaConstant("type");
    public static final SchemaConstant NAME = new SchemaConstant("name");
    public static final SchemaConstant DESCRIPTION = new SchemaConstant("description");
    public static final SchemaConstant PROPERTY_VALUE = new SchemaConstant("value");

    public static final SchemaConstant PROPERTY_ELT = new SchemaConstant("property");

    public static final SchemaConstant PROPERTIES = new SchemaConstant("properties");
    public static final SchemaConstant DEPRECATED = new SchemaConstant("deprecated");


    // ruleset
    public static final SchemaConstant EXCLUDE_PATTERN = new SchemaConstant("exclude-pattern");
    public static final SchemaConstant INCLUDE_PATTERN = new SchemaConstant("include-pattern");
    public static final SchemaConstant RULE = new SchemaConstant("rule");

    public static final SchemaConstant EXCLUDE = new SchemaConstant("exclude");
    public static final SchemaConstant PRIORITY = new SchemaConstant("priority");


    private SchemaConstants() {
        // utility class
    }
}
