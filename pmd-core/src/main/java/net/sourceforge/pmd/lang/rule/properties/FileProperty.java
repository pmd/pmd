/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.io.File;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Property taking a File object as its value.
 *
 * @author Brian Remedios
 * @version Refactored June 2017 (6.0.0)
 */
public final class FileProperty extends AbstractSingleValueProperty<File> {

    /** Factory. */
    public static final PropertyDescriptorFactory<File> FACTORY // @formatter:off
        = new SingleValuePropertyDescriptorFactory<File>(File.class) {
            @Override
            public FileProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                return new FileProperty(nameIn(valuesById),
                                        descriptionIn(valuesById),
                                        null,
                                        0f,
                                        isDefinedExternally);
            }
        }; // @formatter:on


    /**
     * Constructor for file property.
     *
     * @param theName        Name of the property
     * @param theDescription Description
     * @param theDefault     Default value
     * @param theUIOrder     UI order
     */
    public FileProperty(String theName, String theDescription, File theDefault, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder, false);
    }


    /** Master constructor. */
    private FileProperty(String theName, String theDescription, File theDefault, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theDefault, theUIOrder, isDefinedExternally);
    }


    @Override
    public Class<File> type() {
        return File.class;
    }


    @Override
    public File createFrom(String propertyString) {
        return StringUtil.isEmpty(propertyString) ? null : new File(propertyString);
    }
}
