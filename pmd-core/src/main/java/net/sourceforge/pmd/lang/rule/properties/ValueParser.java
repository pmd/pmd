/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

import static net.sourceforge.pmd.lang.rule.properties.modules.MethodPropertyModule.ARRAY_FLAG;
import static net.sourceforge.pmd.lang.rule.properties.modules.MethodPropertyModule.CLASS_METHOD_DELIMITER;
import static net.sourceforge.pmd.lang.rule.properties.modules.MethodPropertyModule.METHOD_ARG_DELIMITER;
import static net.sourceforge.pmd.lang.rule.properties.modules.MethodPropertyModule.METHOD_GROUP_DELIMITERS;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.util.ClassUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Parses a value from a string.
 *
 * @param <U> The type of the value to parse
 */
// FUTURE @FunctionalInterface
public interface ValueParser<U> {

    /** Extracts characters. */
    ValueParser<Character> CHARACTER_PARSER = new ValueParser<Character>() {
        @Override
        public Character valueOf(String value) {
            if (value == null || value.length() != 1) {
                throw new IllegalArgumentException("missing/ambiguous character value");
            }
            return value.charAt(0);
        }
    };

    /** Extracts strings. That's a dummy used to return a list in StringMultiProperty. */
    ValueParser<String> STRING_PARSER = new ValueParser<String>() {
        @Override
        public String valueOf(String value) {
            return value;
        }
    };

    /** Extracts integers. */
    ValueParser<Integer> INTEGER_PARSER = new ValueParser<Integer>() {
        @Override
        public Integer valueOf(String value) {
            return Integer.valueOf(value);
        }
    };

    // FUTURE Integer::valueOf
    /** Extracts booleans. */
    ValueParser<Boolean> BOOLEAN_PARSER = new ValueParser<Boolean>() {
        @Override
        public Boolean valueOf(String value) {
            return Boolean.valueOf(value);
        }
    };

    // FUTURE Boolean::valueOf
    /** Extracts floats. */
    ValueParser<Float> FLOAT_PARSER = new ValueParser<Float>() {
        @Override
        public Float valueOf(String value) {
            return Float.valueOf(value);
        }
    };

    // FUTURE Float::valueOf
    /** Extracts longs. */
    ValueParser<Long> LONG_PARSER = new ValueParser<Long>() {
        @Override
        public Long valueOf(String value) {
            return Long.valueOf(value);
        }
    };

    // FUTURE Long::valueOf
    /** Extracts doubles. */
    ValueParser<Double> DOUBLE_PARSER = new ValueParser<Double>() {
        @Override
        public Double valueOf(String value) {
            return Double.valueOf(value);
        }
    };

    /** Extract classes. */
    ValueParser<Class> CLASS_PARSER = new ValueParser<Class>() {
        @Override
        public Class valueOf(String value) throws IllegalArgumentException {
            if (StringUtil.isEmpty(value)) {
                return null;
            }

            Class<?> cls = ClassUtil.getTypeFor(value);
            if (cls != null) {
                return cls;
            }

            try {
                return Class.forName(value);
            } catch (Exception ex) {
                throw new IllegalArgumentException(value);
            }
        }
    };


    /** Extracts methods. */
    ValueParser<Method> METHOD_PARSER = new ValueParser<Method>() {
        @Override
        public Method valueOf(String value) throws IllegalArgumentException {
            return methodFrom(value, CLASS_METHOD_DELIMITER, METHOD_ARG_DELIMITER);
        }


        /**
         * Returns the method specified within the string argument after parsing out
         * its source class and any optional arguments. Callers need to specify the
         * delimiters expected between the various elements. I.e.:
         *
         * <p>"String#isEmpty()" "String#indexOf(int)" "String#substring(int,int)"
         *
         * <p>If a method isn't part of the specified class we will walk up any
         * superclasses to Object to try and find it.
         *
         * <p>If the classes are listed in the ClassUtil class within in Typemaps then
         * you likely can avoid specifying fully-qualified class names per the above
         * example.
         *
         * <p>Returns null if a matching method cannot be found.
         *
         * @param methodNameAndArgTypes Method name (with its declaring class and arguments)
         * @param classMethodDelimiter  Delimiter between the class and method names
         * @param methodArgDelimiter    Method arguments delimiter
         *
         * @return Method
         */
        public Method methodFrom(String methodNameAndArgTypes, char classMethodDelimiter, char methodArgDelimiter) {

            // classname#methodname(arg1,arg2)
            // 0 1 2

            int delimPos0 = -1;
            if (methodNameAndArgTypes != null) {
                delimPos0 = methodNameAndArgTypes.indexOf(classMethodDelimiter);
            } else {
                return null;
            }

            if (delimPos0 < 0) {
                return null;
            }

            String className = methodNameAndArgTypes.substring(0, delimPos0);
            Class<?> type = ClassUtil.getTypeFor(className);
            if (type == null) {
                return null;
            }

            int delimPos1 = methodNameAndArgTypes.indexOf(METHOD_GROUP_DELIMITERS[0]);
            if (delimPos1 < 0) {
                String methodName = methodNameAndArgTypes.substring(delimPos0 + 1);
                return ClassUtil.methodFor(type, methodName, ClassUtil.EMPTY_CLASS_ARRAY);
            }

            String methodName = methodNameAndArgTypes.substring(delimPos0 + 1, delimPos1);
            if (StringUtil.isEmpty(methodName)) {
                return null;
            } // missing method name?

            int delimPos2 = methodNameAndArgTypes.indexOf(METHOD_GROUP_DELIMITERS[1]);
            if (delimPos2 < 0) {
                return null;
            } // error!

            String argTypesStr = methodNameAndArgTypes.substring(delimPos1 + 1, delimPos2);
            if (StringUtil.isEmpty(argTypesStr)) {
                return ClassUtil.methodFor(type, methodName, ClassUtil.EMPTY_CLASS_ARRAY);
            } // no arg(s)

            String[] argTypeNames = StringUtil.substringsOf(argTypesStr, methodArgDelimiter);
            Class<?>[] argTypes = new Class[argTypeNames.length];
            for (int i = 0; i < argTypes.length; i++) {
                argTypes[i] = typeFor(argTypeNames[i]);
            }

            return ClassUtil.methodFor(type, methodName, argTypes);
        }


        private Class<?> typeFor(String typeName) {

            Class<?> type;

            if (typeName.endsWith(ARRAY_FLAG)) {
                String arrayTypeName = typeName.substring(0, typeName.length() - ARRAY_FLAG.length());
                type = typeFor(arrayTypeName); // recurse
                return Array.newInstance(type, 0).getClass(); // TODO is there a
                // better way to get
                // an array type?
            }

            type = ClassUtil.getTypeFor(typeName); // try shortcut first
            if (type != null) {
                return type;
            }

            try {
                return Class.forName(typeName);
            } catch (Exception ex) {
                return null;
            }
        }

    };


    /**
     * Extracts a primitive from a string.
     *
     * @param value The string to parse
     *
     * @return The primitive found
     *
     * @throws IllegalArgumentException if the value couldn't be parsed
     */
    U valueOf(String value) throws IllegalArgumentException;


    /** Companion object. */
    class Companion {

        private Companion() {

        }


        /**
         * Parses a string into a list of values of type {@literal <U>}.
         *
         * @param toParse   The string to parse
         * @param delimiter The delimiter to use
         * @param extractor The function mapping a string to an instance of {@code <U>}
         * @param <U>       The type of the values to parse
         *
         * @return A list of values
         */
        public static <U> List<U> parsePrimitives(String toParse, char delimiter, ValueParser<U> extractor) {
            String[] values = StringUtil.substringsOf(toParse, delimiter);
            List<U> result = new ArrayList<>();
            for (String s : values) {
                result.add(extractor.valueOf(s));
            }
            return result;
        }
    }

}
