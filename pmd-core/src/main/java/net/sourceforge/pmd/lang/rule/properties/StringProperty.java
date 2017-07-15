/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a datatype that supports single String values.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class StringProperty extends AbstractSingleValueProperty<String> {

    /** Factory. */
    public static final PropertyDescriptorFactory<String> FACTORY // @formatter:off
        = new SingleValuePropertyDescriptorFactory<String>(String.class) {

            @Override
            protected boolean isValueMissing(String value) {
                return StringUtils.isEmpty(value);
            }

            @Override
            public StringProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                return new StringProperty(nameIn(valuesById),
                                          descriptionIn(valuesById),
                                          defaultValueIn(valuesById),
                                          0f,
                                          isDefinedExternally);
            }
        }; // @formatter:on


    /**
     * Constructor.
     *
     * @param theName        Name
     * @param theDescription Description
     * @param defaultValue   Default value
     * @param theUIOrder     UI order
     */
    public StringProperty(String theName, String theDescription, String defaultValue, float theUIOrder) {
        this(theName, theDescription, defaultValue, theUIOrder, false);
    }


    /** Master constructor. */
    private StringProperty(String theName, String theDescription, String defaultValue, float theUIOrder, boolean
        isDefinedExternally) {
        super(theName, theDescription, defaultValue, theUIOrder, isDefinedExternally);
    }


    @Override
    public Class<String> type() {
        return String.class;
    }


    @Override
    public String createFrom(String valueString) {
        return valueString;
    }
}
