/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;

/**
 * Utility class allowing to find the factory of a specific type of descriptor. That's used to define descriptors in
 * the xml, eg for xpath rules.
 *
 * @author Brian Remedios
 */
public final class PropertyDescriptorUtil {

    private static final Map<String, PropertyDescriptorFactory<?>> DESCRIPTOR_FACTORIES_BY_TYPE;


    static {
        Map<String, PropertyDescriptorFactory<?>> temp = new HashMap<>(18);

        temp.put("Boolean", BooleanProperty.FACTORY);
        temp.put("List<Boolean>", BooleanMultiProperty.FACTORY);

        temp.put("String", StringProperty.FACTORY);
        temp.put("List<String>", StringMultiProperty.FACTORY);
        temp.put("Character", CharacterProperty.FACTORY);
        temp.put("List<Character>", CharacterMultiProperty.FACTORY);

        temp.put("Integer", IntegerProperty.FACTORY);
        temp.put("List<Integer>", IntegerMultiProperty.FACTORY);
        temp.put("Long", LongProperty.FACTORY);
        temp.put("List<Long>", LongMultiProperty.FACTORY);
        temp.put("Float", FloatProperty.FACTORY);
        temp.put("List<Float>", FloatMultiProperty.FACTORY);
        temp.put("Double", DoubleProperty.FACTORY);
        temp.put("List<Double>", DoubleMultiProperty.FACTORY);

        //    temp.put("Enum", EnumeratedProperty.FACTORY); // TODO:cf implement that
        //    temp.put("List<Enum>", EnumeratedMultiProperty.FACTORY);

        temp.put("Class", TypeProperty.FACTORY);
        temp.put("List<Class>", TypeMultiProperty.FACTORY);
        temp.put("Method", MethodProperty.FACTORY);
        temp.put("List<Method>", MethodMultiProperty.FACTORY);

        temp.put("File", FileProperty.FACTORY);

        DESCRIPTOR_FACTORIES_BY_TYPE = Collections.unmodifiableMap(temp);
    }


    private PropertyDescriptorUtil() { }


    /**
     * Gets the factory for the descriptor identified by the string id.
     *
     * @param typeId The identifier of the type
     *
     * @return The factory used to build new instances of a descriptor
     */
    public static PropertyDescriptorFactory<?> factoryFor(String typeId) {
        return DESCRIPTOR_FACTORIES_BY_TYPE.get(typeId);
    }


    /**
     * Gets the string representation of this type, as it should be given when defining a descriptor in the xml.
     *
     * @param valueType  The type to look for
     * @param multiValue Whether the descriptor is multivalued or not
     *
     * @return The type id
     */
    public static String typeIdFor(Class<?> valueType, boolean multiValue) {

        for (Map.Entry<String, PropertyDescriptorFactory<?>> entry : DESCRIPTOR_FACTORIES_BY_TYPE.entrySet()) {
            if (entry.getValue().valueType() == valueType && entry.getValue().isMultiValue() == multiValue) {
                return entry.getKey();
            }
        }
        return null;
    }

}
