/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.lang.rule.properties.factories.PropertyDescriptorUtil;

/**
 * Field names for parsing the properties out of the ruleset xml files.
 * 
 * @author Brian Remedios
 * @see RuleSetFactory
 */
public class PropertyDescriptorFields {

    /**
     * The type of the property.
     * @see PropertyDescriptorUtil
     */
    public static final String TYPE = "type";
    /** The name of the property */
    public static final String NAME = "name";
    /** The description of the property. */
    public static final String DESCRIPTION = "description";
    /** The description of the property. */
    public static final String DESC = "description";
    /** The default value. */
    public static final String VALUE = "value";
    /** The default value. */
    public static final String DEFAULT_VALUE = "value";
    /** For multi-valued properties, this defines the delimiter of the single values. */
    public static final String DELIMITER = "delimiter";
    /** The minium allowed value. */
    public static final String MIN = "min";
    /** The maximum allowed value. */
    public static final String MAX = "max";
    /** To limit the range of valid values, e.g. to Enums */
    public static final String LEGAL_PACKAGES = "legalPackages";

    private PropertyDescriptorFields() {}
}
