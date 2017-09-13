/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorField;
import net.sourceforge.pmd.properties.modules.TypePropertyModule;

/**
 * Defines a property that supports multiple class types, even for primitive
 * values!
 *
 * TODO - untested for array types
 *
 * @author Brian Remedios
 */
public final class TypeMultiProperty extends AbstractMultiPackagedProperty<Class> {

    /** Factory. */
    public static final PropertyDescriptorFactory<List<Class>> FACTORY // @formatter:off
        = new MultiValuePropertyDescriptorFactory<Class>(Class.class, PACKAGED_FIELD_TYPES_BY_KEY) {
            @Override
            public TypeMultiProperty createWith(Map<PropertyDescriptorField, String> valuesById, boolean isDefinedExternally) {
                char delimiter = delimiterIn(valuesById);
                return new TypeMultiProperty(nameIn(valuesById),
                                             descriptionIn(valuesById),
                                             defaultValueIn(valuesById),
                                             legalPackageNamesIn(valuesById, delimiter),
                                             0f);
            }
        }; // @formatter:on


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
        this(theName, theDescription, theDefaults, legalPackageNames, theUIOrder, false);

    }


    /** Master constructor. */
    private TypeMultiProperty(String theName, String theDescription, List<Class> theTypeDefaults,
                              String[] legalPackageNames, float theUIOrder, boolean isDefinedExternally) {
        super(theName, theDescription, theTypeDefaults, theUIOrder, isDefinedExternally,
              new TypePropertyModule(legalPackageNames, theTypeDefaults));
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
        this(theName, theDescription, typesFrom(theTypeDefaults),
             legalPackageNames,
             theUIOrder, false);

    }


    private static List<Class> typesFrom(String valueString) {
        return ValueParserConstants.parsePrimitives(valueString, MULTI_VALUE_DELIMITER, ValueParserConstants.CLASS_PARSER);
    }


    @Override
    public Class<Class> type() {
        return Class.class;
    }


    @Override
    public String asString(Class value) {
        return value == null ? "" : value.getName();
    }


    @Override
    protected Class createFrom(String toParse) {
        return ValueParserConstants.CLASS_PARSER.valueOf(toParse);
    }


    @Override
    public List<Class> valueFrom(String valueString) {
        return typesFrom(valueString);
    }
}
