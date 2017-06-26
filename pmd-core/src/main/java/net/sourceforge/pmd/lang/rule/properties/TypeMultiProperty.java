/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class TypeMultiProperty extends AbstractMultiPackagedProperty<Class> {

    public static final PropertyDescriptorFactory FACTORY
        = new BasicPropertyDescriptorFactory<List<Class>>(Class.class, PACKAGED_FIELD_TYPES_BY_KEY) {

        @Override
        public TypeMultiProperty createWith(Map<String, String> valuesById) {
            char delimiter = delimiterIn(valuesById);
            return new TypeMultiProperty(nameIn(valuesById), descriptionIn(valuesById), defaultValueIn(valuesById),
                                         legalPackageNamesIn(valuesById, delimiter), 0f);
        }
    };

    /**
     * Constructor for TypeProperty.
     *
     * @param theName           String
     * @param theDescription    String
     * @param theDefaults       Class[]
     * @param legalPackageNames String[]
     * @param theUIOrder        float
     *
     * @throws IllegalArgumentException
     */
    public TypeMultiProperty(String theName, String theDescription, List<Class> theDefaults,
                             String[] legalPackageNames, float theUIOrder) {
        super(theName, theDescription, theDefaults, legalPackageNames, theUIOrder);

    }

    /**
     * Constructor for TypeProperty.
     *
     * @param theName           String
     * @param theDescription    String
     * @param theDefaults       Class[]
     * @param legalPackageNames String[]
     * @param theUIOrder        float
     *
     * @throws IllegalArgumentException
     */
    public TypeMultiProperty(String theName, String theDescription, Class[] theDefaults,
                             String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription, Arrays.asList(theDefaults), legalPackageNames, theUIOrder);
    }

    /**
     * Constructor for TypeProperty.
     *
     * @param theName           String
     * @param theDescription    String
     * @param theTypeDefaults   String
     * @param legalPackageNames String[]
     * @param theUIOrder        float
     *
     * @throws IllegalArgumentException
     */
    public TypeMultiProperty(String theName, String theDescription, String theTypeDefaults,
                             String[] legalPackageNames, float theUIOrder) {
        this(theName, theDescription, typesFrom(theTypeDefaults), legalPackageNames, theUIOrder);

    }

    public TypeMultiProperty(String theName, String theDescription, String theTypeDefaults,
                             Map<String, String> otherParams, float theUIOrder) {
        this(theName, theDescription, typesFrom(theTypeDefaults), packageNamesIn(otherParams), theUIOrder);
    }

    /**
     * Returns a list of Class objects parsed from the input string.
     *
     * @param classesStr String to parse
     *
     * @return A list of class objects
     */
    public static List<Class> typesFrom(String classesStr) {
        String[] values = StringUtil.substringsOf(classesStr, DELIMITER);

        List<Class> classes = new ArrayList<>(values.length);
        for (int i = 0; i < values.length; i++) {
            classes.add(TypeProperty.classFrom(values[i]));
        }
        return classes;
    }

    @Override
    protected String packageNameOf(Class item) {
        return ((Class<?>) item).getName();
    }

    @Override
    public Class<Class> type() {
        return Class.class;
    }

    @Override
    protected String itemTypeName() {
        return "type";
    }

    @Override
    public String asString(Class value) {
        return value == null ? "" : value.getName();
    }

    @Override
    protected Class createFrom(String toParse) {
        throw new UnsupportedOperationException(); // not used
    }

    @Override
    public List<Class> valueFrom(String valueString) {
        return typesFrom(valueString);
    }
}
