/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sf.saxon.om.AtomicArray;
import net.sf.saxon.om.AtomicSequence;
import net.sf.saxon.om.EmptyAtomicSequence;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.BigIntegerValue;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.FloatValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;
import net.sf.saxon.value.UntypedAtomicValue;


/**
 * Converts Java values into XPath values.
 */
public final class DomainConversion {

    private DomainConversion() {

    }


    public static SchemaType buildType(java.lang.reflect.Type type) {
        switch (type.getTypeName()) {
        case "java.lang.Integer":
        case "java.lang.Long":
            return BuiltInAtomicType.INTEGER;
        case "java.lang.Double":
        case "java.lang.Float":
            return BuiltInAtomicType.DOUBLE;
        case "java.lang.String":
        case "java.lang.Character":
        case "java.lang.Class":
        case "java.util.regex.Pattern":
            return BuiltInAtomicType.STRING;
        default:
            return BuiltInAtomicType.UNTYPED_ATOMIC;
        }
    }

    @NonNull
    public static AtomicSequence convert(Object obj) {
        if (obj instanceof Collection) {
            return getSequenceRepresentation((Collection<?>) obj);
        }
        return getAtomicRepresentation(obj);
    }

    public static SequenceType typeOf(Object obj) {
        if (obj instanceof Collection) {
            if (((Collection<?>) obj).isEmpty()) {
                return SequenceType.EMPTY_SEQUENCE;
            }
            return SequenceType.NON_EMPTY_SEQUENCE;
        } else if (obj instanceof String) {
            return SequenceType.SINGLE_STRING;
        } else if (obj instanceof Boolean) {
            return SequenceType.SINGLE_BOOLEAN;
        } else if (obj instanceof Integer) {
            return SequenceType.SINGLE_INTEGER;
        } else if (obj instanceof Float) {
            return SequenceType.SINGLE_FLOAT;
        } else if (obj instanceof Long) {
            return SequenceType.SINGLE_INTEGER;
        } else if (obj instanceof Double) {
            return SequenceType.SINGLE_DOUBLE;
        } else if (obj instanceof Number) {
            return SequenceType.SINGLE_NUMERIC;
        } else if (obj instanceof Enum<?>) {
            return SequenceType.SINGLE_STRING;
        } else if (obj instanceof Character) {
            return SequenceType.SINGLE_STRING;
        } else if (obj instanceof Pattern) {
            return SequenceType.SINGLE_STRING;
        }
        return SequenceType.SINGLE_ITEM;
    }

    public static AtomicSequence getSequenceRepresentation(Collection<?> list) {
        if (list == null || list.isEmpty()) {
            return EmptyAtomicSequence.getInstance();
        }
        List<AtomicValue> vs = new ArrayList<>(list.size());
        flattenInto(list, vs);
        return new AtomicArray(vs);
    }

    // sequences cannot be nested, this takes care of list of lists,
    // just in case
    private static void flattenInto(Collection<?> list, List<AtomicValue> values) {
        for (Object o : list) {
            if (o instanceof Collection) {
                flattenInto((Collection<?>) o, values);
            } else {
                values.add(getAtomicRepresentation(o));
            }
        }
    }


    /**
     * Gets the Saxon representation of the parameter, if its type corresponds
     * to an XPath 2.0 atomic datatype.
     *
     * @param value The value to convert
     *
     * @return The converted AtomicValue
     */
    @NonNull
    public static AtomicValue getAtomicRepresentation(final Object value) {

        /*
        FUTURE When supported, we should consider refactor this implementation to use Pattern Matching
        (see http://openjdk.java.net/jeps/305) so that it looks clearer.
        */
        if (value == null) {
            return UntypedAtomicValue.ZERO_LENGTH_UNTYPED;

        } else if (value instanceof String) {
            return new StringValue((String) value);
        } else if (value instanceof Boolean) {
            return BooleanValue.get((Boolean) value);
        } else if (value instanceof Integer) {
            return Int64Value.makeIntegerValue((Integer) value);
        } else if (value instanceof Long) {
            return new BigIntegerValue((Long) value);
        } else if (value instanceof Double) {
            return new DoubleValue((Double) value);
        } else if (value instanceof Character) {
            return new StringValue(value.toString());
        } else if (value instanceof Float) {
            return new FloatValue((Float) value);
        } else if (value instanceof Pattern || value instanceof Enum) {
            return new StringValue(String.valueOf(value));
        } else {
            // We could maybe use UntypedAtomicValue
            throw new RuntimeException("Unable to create ValueRepresentation for value of type: " + value.getClass());
        }
    }
}
