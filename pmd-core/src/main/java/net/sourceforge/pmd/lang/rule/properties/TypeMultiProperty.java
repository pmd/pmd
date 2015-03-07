/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Defines a property that supports multiple class types, even for primitive
 * values!
 * 
 * TODO - untested for array types
 *
 * @author Brian Remedios
 */
public class TypeMultiProperty extends AbstractMultiPackagedProperty<Class[]> {

    public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<TypeMultiProperty>(
            Class[].class, PACKAGED_FIELD_TYPES_BY_KEY) {

        public TypeMultiProperty createWith(Map<String, String> valuesById) {
            char delimiter = delimiterIn(valuesById);
            return new TypeMultiProperty(nameIn(valuesById), descriptionIn(valuesById), defaultValueIn(valuesById),
                    legalPackageNamesIn(valuesById, delimiter), 0f);
        }
    };

    /**
     * Constructor for TypeProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param theDefaults Class[]
     * @param legalPackageNames String[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public TypeMultiProperty(String theName, String theDescription, Class<?>[] theDefaults, String[] legalPackageNames,
            float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);

    }

    /**
     * Constructor for TypeProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param theTypeDefaults String
     * @param legalPackageNames String[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public TypeMultiProperty(String theName, String theDescription, String theTypeDefaults, String[] legalPackageNames,
            float theUIOrder) {
        this(theName, theDescription, typesFrom(theTypeDefaults), legalPackageNames, theUIOrder);

    }

    /**
     * Constructor for TypeProperty.
     * 
     * @param theName String
     * @param theDescription String
     * @param theTypeDefaults String
     * @param otherParams Map<String, String>
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public TypeMultiProperty(String theName, String theDescription, String theTypeDefaults,
            Map<String, String> otherParams, float theUIOrder) {
        this(theName, theDescription, typesFrom(theTypeDefaults), packageNamesIn(otherParams), theUIOrder);
    }

    /**
     * @param classesStr String
     * @return Class[]
     */
    public static Class<?>[] typesFrom(String classesStr) {
        String[] values = StringUtil.substringsOf(classesStr, DELIMITER);

        Class<?>[] classes = new Class<?>[values.length];
        for (int i = 0; i < values.length; i++) {
            classes[i] = TypeProperty.classFrom(values[i]);
        }
        return classes;
    }

    /**
     * @param item Object
     * @return String
     */
    @Override
    protected String packageNameOf(Object item) {
        return ((Class<?>) item).getName();
    }

    /**
     * @return Class
     * @see net.sourceforge.pmd.PropertyDescriptor#type()
     */
    public Class<Class[]> type() {
        return Class[].class;
    }

    /**
     * @return String
     */
    @Override
    protected String itemTypeName() {
        return "type";
    }

    /**
     * @param value Object
     * @return String
     */
    @Override
    protected String asString(Object value) {
        return value == null ? "" : ((Class<?>) value).getName();
    }

    /**
     * @param valueString String
     * @return Object
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    public Class<?>[] valueFrom(String valueString) {
        return typesFrom(valueString);
    }
}
