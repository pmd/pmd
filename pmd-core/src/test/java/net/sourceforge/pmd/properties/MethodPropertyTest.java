/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assume;
import org.junit.Test;

import net.sourceforge.pmd.properties.modules.MethodPropertyModule;
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
public class MethodPropertyTest extends AbstractPackagedPropertyDescriptorTester<Method> {

    private static final Method[] ALL_METHODS;

    private static final String[] METHOD_SIGNATURES = {"String#indexOf(int)", "String#substring(int,int)",
                                                       "java.lang.String#substring(int,int)", "Integer#parseInt(String)", "java.util.HashMap#put(Object,Object)",
                                                       "HashMap#containsKey(Object)", };

    static {
        List<Method> allMethods = new ArrayList<>();
        for (Method m : String.class.getDeclaredMethods()) {
            // exclude String.resolveConstantDesc to avoid random test failure with java12
            // there are two methods with the same signature available, but different return types...
            if (!m.getName().equals("resolveConstantDesc")) {
                allMethods.add(m);
            }
        }
        ALL_METHODS = allMethods.toArray(new Method[0]);
    }

    public MethodPropertyTest() {
        super("Method");
    }


    @Override
    @Test
    public void testMissingPackageNames() {
        Map<PropertyDescriptorField, String> attributes = getPropertyDescriptorValues();
        attributes.remove(PropertyDescriptorField.LEGAL_PACKAGES);
        new MethodProperty("p", "d", ALL_METHODS[1], null, 1.0f); // no exception, null is ok
        new MethodMultiProperty("p", "d", new Method[]{ALL_METHODS[2], ALL_METHODS[3]}, null, 1.0f); // no exception, null is ok
    }


    @Test
    public void testAsStringOn() {

        Method method;

        for (String methodSignature : METHOD_SIGNATURES) {
            method = ValueParserConstants.METHOD_PARSER.valueOf(methodSignature);
            assertNotNull("Unable to identify method: " + methodSignature, method);
        }
    }


    @Test
    public void testAsMethodOn() {

        Method[] methods = new Method[METHOD_SIGNATURES.length];

        for (int i = 0; i < METHOD_SIGNATURES.length; i++) {
            methods[i] = ValueParserConstants.METHOD_PARSER.valueOf(METHOD_SIGNATURES[i]);
            assertNotNull("Unable to identify method: " + METHOD_SIGNATURES[i], methods[i]);
        }

        String translatedMethod;
        for (int i = 0; i < methods.length; i++) {
            translatedMethod = MethodPropertyModule.asString(methods[i]);
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
        return new MethodProperty("methodProperty", "asdf", ALL_METHODS[1], new String[]{"java.lang", "org.apache"},
            1.0f);
    }


    @Override
    protected PropertyDescriptor<List<Method>> createMultiProperty() {
        return new MethodMultiProperty("methodProperty", "asdf", new Method[]{ALL_METHODS[2], ALL_METHODS[3]},
            new String[]{"java.lang"}, 1.0f);
    }


    @Override
    protected PropertyDescriptor<Method> createBadProperty() {
        return new MethodProperty("methodProperty", "asdf", ALL_METHODS[1], new String[]{"java.util"}, 1.0f);

    }


    @Override
    protected PropertyDescriptor<List<Method>> createBadMultiProperty() {
        return new MethodMultiProperty("methodProperty", "asdf", new Method[]{ALL_METHODS[2], ALL_METHODS[3]},
            new String[]{"java.util"}, 1.0f);
    }


    @Override
    @Test
    public void testFactorySingleValue() {
        Assume.assumeTrue("MethodProperty cannot be built from XPath (#762)", false);
    }


    @Override
    @Test
    public void testFactoryMultiValueCustomDelimiter() {
        Assume.assumeTrue("MethodProperty cannot be built from XPath (#762)", false);
    }


    @Override
    @Test
    public void testFactoryMultiValueDefaultDelimiter() {
        Assume.assumeTrue("MethodProperty cannot be built from XPath (#762)", false);
    }

}
