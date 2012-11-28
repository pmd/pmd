/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.TestBase;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

/**
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class RuleSetToDocsTest extends TestBase {

    @Test
    public void convertRulesetsTest() throws Exception {
	RuleSetToDocs builder = new RuleSetToDocs();
	builder.setRulesDirectory(TEST_DIR + "src/main/resources/rulesets");
	builder.setTargetDirectory(TEST_DIR + "target");
	builder.setRuntimeClasspath(new URL[] {new File("target/test-classes").toURI().toURL()});

	builder.convertRulesets();

	String codeSizeRuleset = IOUtils.toString(new File(TEST_DIR + "target/java/codesize.xml").toURI());
	assertTrue(codeSizeRuleset.contains("minimum"));
    }

    @Test
    public void readPropertyDescriptors() throws Exception {
	ClassLoader cl = RuleSetToDocsTest.class.getClassLoader();
	Class<?> clazz = cl.loadClass("net.sourceforge.pmd.lang.java.rule.codesize.NPathComplexityRule");
	Object ruleInstance = clazz.newInstance();
	Class<?> propertySource = cl.loadClass("net.sourceforge.pmd.AbstractPropertySource");
	Class<?> propertyDesc = cl.loadClass("net.sourceforge.pmd.PropertyDescriptor");
	Method nameMethod = propertyDesc.getDeclaredMethod("name");
	Method descMethod = propertyDesc.getDeclaredMethod("description");
	Field propertiesField = propertySource.getDeclaredField("propertyDescriptors");
	Field propertiesValues = propertySource.getDeclaredField("propertyValuesByDescriptor");
	propertiesField.setAccessible(true);
	propertiesValues.setAccessible(true);
	@SuppressWarnings("rawtypes")
	List properties = (List)propertiesField.get(ruleInstance);
	@SuppressWarnings("rawtypes")
	Map values = (Map)propertiesValues.get(ruleInstance);
	assertEquals(1, properties.size());
	assertEquals("minimum", nameMethod.invoke(properties.get(0)));
	assertEquals("The minimum threshold property.", descMethod.invoke(properties.get(0)));
	assertEquals("200.0", String.valueOf(values.get(properties.get(0))));
    }
}
