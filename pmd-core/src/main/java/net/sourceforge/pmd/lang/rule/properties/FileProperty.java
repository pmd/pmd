/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.io.File;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * 
 * @author Brian Remedios
 */
public class FileProperty extends AbstractProperty<File> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<FileProperty>(File.class) {

        public FileProperty createWith(Map<String, String> valuesById) {
            return new FileProperty(nameIn(valuesById), descriptionIn(valuesById), null, 0f);
        }
    };

    public FileProperty(String theName, String theDescription, File theDefault, float theUIOrder) {
        super(theName, theDescription, theDefault, theUIOrder);
    }

    public Class<File> type() {
        return File.class;
    }

    public File valueFrom(String propertyString) throws IllegalArgumentException {

        return StringUtil.isEmpty(propertyString) ? null : new File(propertyString);
    }

    @Override
    protected String defaultAsString() {
        // TODO Auto-generated method stub
        return null;
    }

}
