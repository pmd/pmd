/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.util.StringUtil;

public class PropertyDescriptorFactory {
    /**
     * Returns the String type of the PropertyDescriptor for use in XML
     * serialization. If the value is <code>null</code> the type cannot be
     * serialized.
     */
    public static String getPropertyDescriptorType(PropertyDescriptor<?> propertyDescriptor) {
        Class<?> type = propertyDescriptor.type();
        String typeName = null;
        if (propertyDescriptor instanceof EnumeratedProperty || propertyDescriptor instanceof MethodProperty // TODO
                                                                                                             // -
                                                                                                             // yes
                                                                                                             // we
                                                                                                             // can,
                                                                                                             // investigate
                || propertyDescriptor instanceof TypeProperty) {
            // Cannot serialize these kinds of PropertyDescriptors
        } else if ("java.lang".equals(type.getPackage().getName())) {
            typeName = type.getSimpleName();
        }
        if (typeName == null) {
            throw new IllegalArgumentException("Cannot encode type for PropertyDescriptor class: " + type.getName());
        }
        return typeName;
    }

    public static PropertyDescriptor<?> createPropertyDescriptor(String name, String description, String type,
            String delimiter, String min, String max, String value) {
        return new PropertyDescriptorWrapper(createRawPropertyDescriptor(name, description, type, delimiter, min, max,
                value));
    }

    private static PropertyDescriptor<?> createRawPropertyDescriptor(String name, String description, String type,
            String delimiter, String min, String max, String value) {
        if ("Boolean".equals(type)) {
            return new BooleanProperty(name, description, value, 0.0f);
        } else if ("Boolean[]".equals(type)) {
            BooleanMultiProperty property = new BooleanMultiProperty(name, description, null, 0.0f);
            return new BooleanMultiProperty(name, description, property.valueFrom(value), 0.0f);
        } else if ("Character".equals(type)) {
            return new CharacterProperty(name, description, CharacterProperty.charFrom(value), 0.0f);
        } else if ("Character[]".equals(type)) {
            checkDelimiter(name, type, delimiter);
            char delim = delimiter.charAt(0);
            CharacterMultiProperty property = new CharacterMultiProperty(name, description, null, 0.0f, delim);
            return new CharacterMultiProperty(name, description, property.valueFrom(value), 0.0f, delim);
        } else if ("Double".equals(type)) {
            checkMinMax(name, type, min, max);
            return new DoubleProperty(name, description, min, max, value, 0.0f);
        } else if ("Double[]".equals(type)) {
            checkMinMax(name, type, min, max);
            DoubleMultiProperty property = new DoubleMultiProperty(name, description, 0d, 0d, null, 0.0f);
            return new DoubleMultiProperty(name, description, DoubleProperty.doubleFrom(min),
                    DoubleProperty.doubleFrom(max), property.valueFrom(value), 0.0f);
        } else if ("Float".equals(type)) {
            checkMinMax(name, type, min, max);
            return new FloatProperty(name, description, min, max, value, 0.0f);
        } else if ("Float[]".equals(type)) {
            checkMinMax(name, type, min, max);
            FloatMultiProperty property = new FloatMultiProperty(name, description, 0f, 0f, null, 0.0f);
            return new FloatMultiProperty(name, description, FloatProperty.floatFrom(min),
                    FloatProperty.floatFrom(max), property.valueFrom(value), 0.0f);
        } else if ("Integer".equals(type)) {
            checkMinMax(name, type, min, max);
            return new IntegerProperty(name, description, min, max, value, 0.0f);
        } else if ("Integer[]".equals(type)) {
            checkMinMax(name, type, min, max);
            IntegerMultiProperty property = new IntegerMultiProperty(name, description, 0, 0, null, 0.0f);
            return new IntegerMultiProperty(name, description, IntegerProperty.intFrom(min),
                    IntegerProperty.intFrom(max), property.valueFrom(value), 0.0f);
        } else if ("Long".equals(type)) {
            checkMinMax(name, type, min, max);
            return new LongProperty(name, description, min, max, value, 0.0f);
        } else if ("Long[]".equals(type)) {
            checkMinMax(name, type, min, max);
            LongMultiProperty property = new LongMultiProperty(name, description, 0L, 0L, null, 0.0f);
            return new LongMultiProperty(name, description, LongProperty.longFrom(min), LongProperty.longFrom(max),
                    property.valueFrom(value), 0.0f);

            // TODO - include legal package names for next four types
        } else if ("Type".equals(type)) {
            return new TypeProperty(name, description, value, (String[]) null, 0.0f);
        } else if ("Type[]".equals(type)) {
            return new TypeMultiProperty(name, description, value, (String[]) null, 0.0f);
        } else if ("Method".equals(type)) {
            return new MethodProperty(name, description, value, (String[]) null, 0.0f);
        } else if ("Method[]".equals(type)) {
            return new MethodMultiProperty(name, description, value, (String[]) null, 0.0f);

        } else if ("String".equals(type)) {
            return new StringProperty(name, description, value, 0.0f);
        } else if ("String[]".equals(type)) {
            checkDelimiter(name, type, delimiter);
            char delim = delimiter.charAt(0);
            StringMultiProperty property = new StringMultiProperty(name, description, null, 0.0f, delim);
            return new StringMultiProperty(name, description, property.valueFrom(value), 0.0f, delim);
        } else {
            throw new IllegalArgumentException("Cannot define property type '" + type + "'.");
        }
    }

    private static void checkDelimiter(String name, String type, String delimiter) {
        if (delimiter == null || delimiter.length() == 0) {
            throw new IllegalArgumentException("Delimiter must be provided to create PropertyDescriptor for " + name
                    + " of type " + type + ".");
        }
    }

    private static void checkMinMax(String name, String type, String min, String max) {
        if (StringUtil.isEmpty(min)) {
            throw new IllegalArgumentException("Min must be provided to create PropertyDescriptor for " + name
                    + " of type " + type + ".");
        }
        if (StringUtil.isEmpty(max)) {
            throw new IllegalArgumentException("Max must be provided to create PropertyDescriptor for " + name
                    + " of type " + type + ".");
        }
    }
}
