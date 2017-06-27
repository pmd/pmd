/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.MethodMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.MethodProperty;
import net.sourceforge.pmd.util.ClassUtil;

/**
 * Evaluates the functionality of the MethodProperty descriptor by testing its
 * ability to catch creation errors (illegal args), flag invalid methods per the
 * allowable packages, and serialize/deserialize groups of methods onto/from a
 * string buffer.
 *
 * We're using methods from java.lang classes for 'normal' constructors and
 * applying ones from java.util types as ones we expect to fail.
 *
 * @author Brian Remedios
 */
public class MethodPropertyTest extends AbstractPropertyDescriptorTester<Method> {

    private static final Method[] ALL_METHODS = String.class.getDeclaredMethods();

    private static final String[] METHOD_SIGNATURES = {"String#indexOf(int)", "String#substring(int,int)",
                                                       "java.lang.String#substring(int,int)", "Integer#parseInt(String)", "java.util.HashMap#put(Object,Object)",
                                                       "HashMap#containsKey(Object)", };

    public MethodPropertyTest() {
        super("Method");
    }

    @Test
    public void testAsStringOn() {

        Method method = null;

        for (int i = 0; i < METHOD_SIGNATURES.length; i++) {
            method = MethodProperty.methodFrom(METHOD_SIGNATURES[i], MethodProperty.CLASS_METHOD_DELIMITER,
                                               MethodProperty.METHOD_ARG_DELIMITER);
            assertNotNull("Unable to identify method: " + METHOD_SIGNATURES[i], method);
        }
    }

    @Test
    public void testAsMethodOn() {

        Method[] methods = new Method[METHOD_SIGNATURES.length];

        for (int i = 0; i < METHOD_SIGNATURES.length; i++) {
            methods[i] = MethodProperty.methodFrom(METHOD_SIGNATURES[i], MethodProperty.CLASS_METHOD_DELIMITER,
                                                   MethodProperty.METHOD_ARG_DELIMITER);
            assertNotNull("Unable to identify method: " + METHOD_SIGNATURES[i], methods[i]);
        }

        String translatedMethod = null;
        for (int i = 0; i < methods.length; i++) {
            translatedMethod = MethodProperty.asStringFor(methods[i]);
            assertTrue("Translated method does not match", ClassUtil.withoutPackageName(METHOD_SIGNATURES[i])
                                                                    .equals(ClassUtil.withoutPackageName(translatedMethod)));
        }
    }

    @Override
    protected Method createValue() {
        return randomChoice(ALL_METHODS);
    }

    @Override
    protected Method createBadValue() {
        return randomChoice(HashMap.class.getDeclaredMethods());
    }

    @Override
    protected PropertyDescriptor<Method> createProperty() {
        return new MethodProperty("methodProperty", "asdf", ALL_METHODS[1], new String[] {"java.lang"}, 1.0f);
    }

    @Override
    protected PropertyDescriptor<List<Method>> createMultiProperty() {
        return new MethodMultiProperty("methodProperty", "asdf", new Method[] {ALL_METHODS[2], ALL_METHODS[3]},
                                       new String[] {"java.lang"}, 1.0f);
    }

    @Override
    protected PropertyDescriptor<Method> createBadProperty() {
        return new MethodProperty("methodProperty", "asdf", ALL_METHODS[1], new String[] {"java.util"}, 1.0f);

    }

    @Override
    protected PropertyDescriptor<List<Method>> createBadMultiProperty() {
        return new MethodMultiProperty("methodProperty", "asdf", new Method[] {ALL_METHODS[2], ALL_METHODS[3]},
                                       new String[] {"java.util"}, 1.0f);
    }
}
