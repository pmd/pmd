/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.factories.BasicPropertyDescriptorFactory;
import net.sourceforge.pmd.util.ClassUtil;

/**
 * Defines a property that supports single class types, even for primitive values!
 * 
 * TODO - untested for array types
 *
 * @author Brian Remedios
 */
public class TypeProperty extends AbstractPackagedProperty<Class> {

	public static final PropertyDescriptorFactory FACTORY = new BasicPropertyDescriptorFactory<TypeProperty>(Class.class, packagedFieldTypesByKey) {

		public TypeProperty createWith(Map<String, String> valuesById) {
			return new TypeProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					legalPackageNamesIn(valuesById),
					0f);
		}
	};
	
    /**
     * Constructor for TypeProperty.
     * @param theName String
     * @param theDescription String
     * @param theDefault Class
     * @param legalPackageNames String[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public TypeProperty(String theName, String theDescription, Class<?> theDefault, String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefault, legalPackageNames, theUIOrder);
    }

    /**
     * 
     * @param theName String
     * @param theDescription String
     * @param defaultTypeStr String
     * @param legalPackageNames String[]
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public TypeProperty(String theName, String theDescription, String defaultTypeStr, String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription, classFrom(defaultTypeStr), legalPackageNames, theUIOrder);
    }
    
    /**
     * 
     * @param theName String
     * @param theDescription String
     * @param defaultTypeStr String
     * @param otherParams Map<String, String>
     * @param theUIOrder float
     * @throws IllegalArgumentException
     */
    public TypeProperty(String theName, String theDescription, String defaultTypeStr, Map<String, String> otherParams, float theUIOrder) {
        this(theName, theDescription, classFrom(defaultTypeStr), packageNamesIn(otherParams), theUIOrder);
    }
    
    /**
     * @return String
     */
    protected String defaultAsString() {
        return asString(defaultValue());
    }
    
    /**
     * Method packageNameOf.
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
    public Class<Class> type() {
        return Class.class;
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
     * @param className String
     * @return Class
     * @throws IllegalArgumentException
     */
    static Class<?> classFrom(String className) {

        Class<?> cls = ClassUtil.getTypeFor(className);
        if (cls != null) {
            return cls;
        }

        try {
            return Class.forName(className);
        } catch (Exception ex) {
            throw new IllegalArgumentException(className);
        }
    }

    /**
     * @param valueString String
     * @return Object
     * @see net.sourceforge.pmd.PropertyDescriptor#valueFrom(String)
     */
    public Class<?> valueFrom(String valueString) {
        return classFrom(valueString);
    }
}
