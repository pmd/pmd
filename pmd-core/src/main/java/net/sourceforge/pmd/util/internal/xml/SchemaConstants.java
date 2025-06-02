/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.internal.xml;


/**
 * Constants of the ruleset schema.
 */
public final class SchemaConstants {

    public static final SchemaConstant PROPERTY_TYPE = new SchemaConstant("type");
    public static final SchemaConstant NAME = new SchemaConstant("name");
    public static final SchemaConstant MESSAGE = new SchemaConstant("message");
    public static final SchemaConstant LANGUAGE = new SchemaConstant("language");
    public static final SchemaConstant CLASS = new SchemaConstant("class");
    public static final SchemaConstant DESCRIPTION = new SchemaConstant("description");
    public static final SchemaConstant PROPERTY_VALUE = new SchemaConstant("value");

    public static final SchemaConstant PROPERTIES = new SchemaConstant("properties");
    public static final SchemaConstant PROPERTY_ELT = new SchemaConstant("property");
    public static final SchemaConstant DEPRECATED = new SchemaConstant("deprecated");

    public static final SchemaConstant RULESET = new SchemaConstant("ruleset");
    public static final SchemaConstant EXCLUDE_PATTERN = new SchemaConstant("exclude-pattern");
    public static final SchemaConstant INCLUDE_PATTERN = new SchemaConstant("include-pattern");
    public static final SchemaConstant RULE = new SchemaConstant("rule");
    public static final SchemaConstant REF = new SchemaConstant("ref");
    public static final SchemaConstant EXCLUDE = new SchemaConstant("exclude");
    public static final SchemaConstant PRIORITY = new SchemaConstant("priority");
    public static final SchemaConstant MINIMUM_LANGUAGE_VERSION = new SchemaConstant("minimumLanguageVersion");
    public static final SchemaConstant MAXIMUM_LANGUAGE_VERSION = new SchemaConstant("maximumLanguageVersion");
    public static final SchemaConstant EXTERNAL_INFO_URL = new SchemaConstant("externalInfoUrl");
    public static final SchemaConstant EXAMPLE = new SchemaConstant("example");
    public static final SchemaConstant SINCE = new SchemaConstant("since");
    public static final SchemaConstant DELIMITER = new SchemaConstant("delimiter");


    public static final SchemaConstant PROPERTY_MIN = new SchemaConstant("min");
    public static final SchemaConstant PROPERTY_MAX = new SchemaConstant("max");

    private SchemaConstants() {
        // utility class
    }
}
