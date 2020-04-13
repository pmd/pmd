/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath.internal;

import java.util.Collection;
import java.util.regex.Pattern;

import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.SchemaType;
import net.sf.saxon.value.AtomicValue;
import net.sf.saxon.value.BigIntegerValue;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.FloatValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.SequenceExtent;
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

    public static Sequence getSequenceRepresentation(Collection<?> list) {
        if (list == null || list.isEmpty()) {
            return EmptySequence.getInstance();
        }
        Item[] items = list.stream()
                           .map(DomainConversion::getAtomicRepresentation)
                           .toArray(Item[]::new);
        return new SequenceExtent(items);
    }


    /**
     * Gets the Saxon representation of the parameter, if its type corresponds
     * to an XPath 2.0 atomic datatype.
     *
     * @param value The value to convert
     *
     * @return The converted AtomicValue
     */
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
        } else if (value instanceof Pattern) {
            return new StringValue(String.valueOf(value));
        } else {
            // We could maybe use UntypedAtomicValue
            throw new RuntimeException("Unable to create ValueRepresentation for value of type: " + value.getClass());
        }
    }
}
