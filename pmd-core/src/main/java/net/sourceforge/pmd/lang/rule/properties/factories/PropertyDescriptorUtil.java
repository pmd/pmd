/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties.factories;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.lang.rule.properties.BooleanMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.CharacterMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.CharacterProperty;
import net.sourceforge.pmd.lang.rule.properties.DoubleMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.DoubleProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.FileProperty;
import net.sourceforge.pmd.lang.rule.properties.FloatMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.FloatProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.LongMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.LongProperty;
import net.sourceforge.pmd.lang.rule.properties.MethodMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.MethodProperty;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.lang.rule.properties.TypeMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.TypeProperty;

/**
 *
 * @author Brian Remedios
 */
public class PropertyDescriptorUtil {

    public static final Comparator<PropertyDescriptor<?>> COMPARATOR_BY_ORDER = new Comparator<PropertyDescriptor<?>>() {
        @Override
        public int compare(PropertyDescriptor<?> pd1, PropertyDescriptor<?> pd2) {
            return pd2.uiOrder() > pd1.uiOrder() ? -1 : 1;
        }
    };

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

        temp.put("Enum", EnumeratedProperty.FACTORY);
        temp.put("List<Enum>", EnumeratedMultiProperty.FACTORY);

        temp.put("Class", TypeProperty.FACTORY);
        temp.put("List<Class>", TypeMultiProperty.FACTORY);
        temp.put("Method", MethodProperty.FACTORY);
        temp.put("List<Method>", MethodMultiProperty.FACTORY);

        temp.put("File", FileProperty.FACTORY);

        DESCRIPTOR_FACTORIES_BY_TYPE = Collections.unmodifiableMap(temp);
    }

    private PropertyDescriptorUtil() { }

    public static PropertyDescriptorFactory<?> factoryFor(String typeId) {
        return DESCRIPTOR_FACTORIES_BY_TYPE.get(typeId);
    }

    public static String typeIdFor(Class<?> valueType) {

        // a reverse lookup, not very efficient but fine for now
        for (Map.Entry<String, PropertyDescriptorFactory<?>> entry : DESCRIPTOR_FACTORIES_BY_TYPE.entrySet()) {
            if (entry.getValue().valueType() == valueType) {
                return entry.getKey();
            }
        }
        return null;
    }

}
