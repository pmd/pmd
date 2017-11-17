/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.properties.builders.PropertyDescriptorExternalBuilder;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class PropertyDescriptorUtil {

    private static final Map<String, PropertyDescriptorExternalBuilder<?>> DESCRIPTOR_FACTORIES_BY_TYPE;


    static {
        Map<String, PropertyDescriptorExternalBuilder<?>> temp = new HashMap<>(18);
        temp.put("Boolean", BooleanProperty.extractor());
        temp.put("List[Boolean]", BooleanMultiProperty.extractor());

        temp.put("String", StringProperty.extractor());
        temp.put("List[String]", StringMultiProperty.extractor());
        temp.put("Character", CharacterProperty.extractor());
        temp.put("List[Character]", CharacterMultiProperty.extractor());


        temp.put("Integer", IntegerProperty.extractor());
        temp.put("List[Integer]", IntegerMultiProperty.extractor());
        temp.put("Long", LongProperty.extractor());
        temp.put("List[Long]", LongMultiProperty.extractor());
        temp.put("Float", FloatProperty.extractor());
        temp.put("List[Float]", FloatMultiProperty.extractor());
        temp.put("Double", DoubleProperty.extractor());
        temp.put("List[Double]", DoubleMultiProperty.extractor());
        //    temp.put("Enum", EnumeratedProperty.FACTORY); // TODO:cf implement that
        //    temp.put("List[Enum]", EnumeratedMultiProperty.FACTORY);

        temp.put("Class", TypeProperty.extractor());
        temp.put("List[Class]", TypeMultiProperty.extractor());
        temp.put("Method", MethodProperty.extractor());
        temp.put("List[Method]", MethodMultiProperty.extractor());

        temp.put("File", FileProperty.extractor());

        DESCRIPTOR_FACTORIES_BY_TYPE = Collections.unmodifiableMap(temp);
    }


    private PropertyDescriptorUtil() {
    }


    /**
     * Gets the factory for the descriptor identified by the string id.
     *
     * @param typeId The identifier of the type
     *
     * @return The factory used to build new instances of a descriptor
     */
    public static PropertyDescriptorExternalBuilder<?> factoryFor(String typeId) {
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

        for (Map.Entry<String, PropertyDescriptorExternalBuilder<?>> entry : DESCRIPTOR_FACTORIES_BY_TYPE.entrySet()) {
            if (entry.getValue().valueType() == valueType && entry.getValue().isMultiValue() == multiValue) {
                return entry.getKey();
            }
        }
        return null;
    }


}
