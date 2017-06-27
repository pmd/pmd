/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.io.File;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Property taking a File object as its value.
 *
 * @author Brian Remedios
 */
public class FileProperty extends AbstractSingleValueProperty<File> {

    /** Factory. */
    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<File>(File.class) {
        @Override
        public FileProperty createWith(Map<PropertyDescriptorField, String> valuesById) {
            return new FileProperty(nameIn(valuesById), descriptionIn(valuesById), null, 0f);
        }
    };


    public FileProperty(String theName, String theDescription, File theDefault, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);
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
