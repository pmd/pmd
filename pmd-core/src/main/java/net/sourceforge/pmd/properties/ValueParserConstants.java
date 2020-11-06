/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static net.sourceforge.pmd.properties.modules.MethodPropertyModule.ARRAY_FLAG;
import static net.sourceforge.pmd.properties.modules.MethodPropertyModule.CLASS_METHOD_DELIMITER;
import static net.sourceforge.pmd.properties.modules.MethodPropertyModule.METHOD_ARG_DELIMITER;
import static net.sourceforge.pmd.properties.modules.MethodPropertyModule.METHOD_GROUP_DELIMITERS;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.util.ClassUtil;


/**
 * This class will be completely scrapped with 7.0.0. It only hid away the syntactic
 * overhead caused by the lack of lambdas in Java 7.
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated Was internal API
 */
@Deprecated
@InternalApi
public final class ValueParserConstants {


    /** Extracts methods. */
    static final ValueParser<Method> METHOD_PARSER = new ValueParser<Method>() {
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
        Method methodFrom(String methodNameAndArgTypes, char classMethodDelimiter, char methodArgDelimiter) {

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
            if (StringUtils.isBlank(methodName)) {
                return null;
            } // missing method name?

            int delimPos2 = methodNameAndArgTypes.indexOf(METHOD_GROUP_DELIMITERS[1]);
            if (delimPos2 < 0) {
                return null;
            } // error!

            String argTypesStr = methodNameAndArgTypes.substring(delimPos1 + 1, delimPos2);
            if (StringUtils.isBlank(argTypesStr)) {
                return ClassUtil.methodFor(type, methodName, ClassUtil.EMPTY_CLASS_ARRAY);
            } // no arg(s)

            String[] argTypeNames = StringUtils.split(argTypesStr, methodArgDelimiter);
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
            } catch (ClassNotFoundException ex) {
                return null;
            }
        }

    };
    /** Extracts characters. */
    static final ValueParser<Character> CHARACTER_PARSER = new ValueParser<Character>() {
        @Override
        public Character valueOf(String value) {
            if (value == null || value.length() != 1) {
                throw new IllegalArgumentException("missing/ambiguous character value");
            }
            return value.charAt(0);
        }
    };
    /** Extracts strings. That's a dummy used to return a list in StringMultiProperty. */
    static final ValueParser<String> STRING_PARSER = new ValueParser<String>() {
        @Override
        public String valueOf(String value) {
            return value;
        }
    };
    /** Extracts integers. */
    static final ValueParser<Integer> INTEGER_PARSER = new ValueParser<Integer>() {
        @Override
        public Integer valueOf(String value) {
            return Integer.valueOf(value);
        }
    };
    /** Extracts booleans. */
    static final ValueParser<Boolean> BOOLEAN_PARSER = new ValueParser<Boolean>() {
        @Override
        public Boolean valueOf(String value) {
            return Boolean.valueOf(value);
        }
    };
    /** Extracts floats. */
    static final ValueParser<Float> FLOAT_PARSER = new ValueParser<Float>() {
        @Override
        public Float valueOf(String value) {
            return Float.valueOf(value);
        }
    };
    /** Extracts longs. */
    static final ValueParser<Long> LONG_PARSER = new ValueParser<Long>() {
        @Override
        public Long valueOf(String value) {
            return Long.valueOf(value);
        }
    };
    /** Extracts doubles. */
    static final ValueParser<Double> DOUBLE_PARSER = new ValueParser<Double>() {
        @Override
        public Double valueOf(String value) {
            return Double.valueOf(value);
        }
    };
    /** Extracts files */
    static final ValueParser<File> FILE_PARSER = new ValueParser<File>() {
        @Override
        public File valueOf(String value) throws IllegalArgumentException {
            return new File(value);
        }
    };

    /** Compiles a regex. */
    static final ValueParser<Pattern> REGEX_PARSER = new ValueParser<Pattern>() {
        @Override
        public Pattern valueOf(String value) throws IllegalArgumentException {
            return Pattern.compile(value);
        }
    };

    /** Extract classes. */
    static final ValueParser<Class> CLASS_PARSER = new ValueParser<Class>() {
        @Override
        public Class valueOf(String value) throws IllegalArgumentException {
            if (StringUtils.isBlank(value)) {
                return null;
            }

            Class<?> cls = ClassUtil.getTypeFor(value);
            if (cls != null) {
                return cls;
            }

            try {
                return Class.forName(value);
            } catch (ClassNotFoundException ex) {
                throw new IllegalArgumentException(value);
            }
        }
    };


    private ValueParserConstants() {

    }


    static <T> ValueParser<T> enumerationParser(final Map<String, T> mappings) {

        if (mappings.containsValue(null)) {
            throw new IllegalArgumentException("Map may not contain entries with null values");
        }

        return new ValueParser<T>() {
            @Override
            public T valueOf(String value) throws IllegalArgumentException {
                if (!mappings.containsKey(value)) {
                    throw new IllegalArgumentException("Value was not in the set " + mappings.keySet());
                }
                return mappings.get(value);
            }
        };
    }


    /**
     * Returns a value parser parsing lists of values of type U.
     *
     * @param parser    Parser used to parse a single value
     * @param delimiter Char delimiting values
     * @param <U>       Element type of the target list
     *
     * @return A list of values
     */
    public static <U> ValueParser<List<U>> multi(final ValueParser<U> parser, final char delimiter) {
        return new ValueParser<List<U>>() {
            @Override
            public List<U> valueOf(String value) throws IllegalArgumentException {
                return parsePrimitives(value, delimiter, parser);
            }
        };
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
    // FUTURE 1.8 : use java.util.function.Function<String, U> in place of ValueParser<U>,
    // replace ValueParser constants with static functions
    static <U> List<U> parsePrimitives(String toParse, char delimiter, ValueParser<U> extractor) {
        String[] values = StringUtils.split(toParse, delimiter);
        List<U> result = new ArrayList<>();
        for (String s : values) {
            result.add(extractor.valueOf(s));
        }
        return result;
    }

}
