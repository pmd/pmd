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


    private SchemaConstants() {
        // utility class
    }
}
