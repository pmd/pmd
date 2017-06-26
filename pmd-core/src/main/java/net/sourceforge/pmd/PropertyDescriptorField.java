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
public enum PropertyDescriptorField {

    /**
     * The type of the property.
     *
     * @see PropertyDescriptorUtil
     */
    TYPE("type"),
    /** The name of the property. */
    NAME("name"),
    /** The description of the property. */
    DESCRIPTION("description"),
    /** The description of the property. */
    DESC("description"),
    /** The default value. */
    VALUE("value"),
    /** The default value. */
    DEFAULT_VALUE("value"),
    /** For multi-valued properties, this defines the delimiter of the single values. */
    DELIMITER("delimiter"),
    /** The minium allowed value. */
    MIN("min"),
    /** The maximum allowed value. */
    MAX("max"),
    /** To limit the range of valid values, {@literal e.g.} to Enums. */
    LEGAL_PACKAGES("legalPackages"),
    /** Labels for enumerated properties. */
    LABELS("labels"),
    /** Choices for enumerated properties. */
    CHOICES("choices"),
    /** Default index for enumerated properties. */
    DEFAULT_INDEX("defaultIndex");

    final String representation;

    PropertyDescriptorField(String representation) {
        this.representation = representation;
    }

    @Override
    public String toString() {
        return representation;
    }

}
