/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.Set;

/**
 * Evaluates the functionality of the TypeProperty descriptor by testing its
 * ability to catch creation errors (illegal args), flag invalid Type values per
 * the allowable packages, and serialize/deserialize groups of types onto/from a
 * string buffer.
 *
 * We're using java.lang classes for 'normal' constructors and applying
 * java.util types as ones we expect to fail.
 *
 * @author Brian Remedios
 */
public class TypePropertyTest extends AbstractPackagedPropertyDescriptorTester<Class> {

    private static final List<Class> JAVA_LANG_CLASSES = Arrays.<Class>asList(String.class, Integer.class, Thread.class,
                                                                              Object.class, Runtime.class);
    private static final List<Class> JAVA_UTIL_CLASSES = Arrays.<Class>asList(HashMap.class, Map.class,
                                                                              Comparator.class, Set.class,
                                                                              Observer.class);


    public TypePropertyTest() {
        super("Class");
    }


    @Override
    protected Class createBadValue() {
        return JAVA_UTIL_CLASSES.get(randomInt(0, JAVA_UTIL_CLASSES.size()));
    }


    @Override
    protected PropertyDescriptor<Class> createProperty() {
        return new TypeProperty("testType", "Test type property", createValue(), new String[] {"java.lang"},
                                1.0f);
    }


    @Override
    protected Class createValue() {
        return JAVA_LANG_CLASSES.get(randomInt(0, JAVA_LANG_CLASSES.size()));
    }


    @Override
    protected PropertyDescriptor<List<Class>> createMultiProperty() {
        return new TypeMultiProperty("testType", "Test type property", JAVA_LANG_CLASSES, new String[] {"java.lang"},
                                     1.0f);
    }


    @Override
    protected PropertyDescriptor<Class> createBadProperty() {
        return new TypeProperty("testType", "Test type property", createValue(), new String[] {"java.util"},
                                1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Class>> createBadMultiProperty() {
        return new TypeMultiProperty("testType", "Test type property", Collections.<Class>singletonList(Set.class),
                                     new String[] {"java.lang"}, 1.0f);
    }
}
