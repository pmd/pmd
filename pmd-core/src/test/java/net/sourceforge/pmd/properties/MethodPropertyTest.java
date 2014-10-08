package net.sourceforge.pmd.properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.HashMap;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.lang.rule.properties.MethodMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.MethodProperty;
import net.sourceforge.pmd.util.ClassUtil;

import org.junit.Test;

/**
 * Evaluates the functionality of the MethodProperty descriptor by testing its ability to catch creation
 * errors (illegal args), flag invalid methods per the allowable packages, and serialize/deserialize
 * groups of methods onto/from a string buffer.
 * 
 * We're using methods from java.lang classes for 'normal' constructors and applying ones from
 * java.util types as ones we expect to fail.
 * 
 * @author Brian Remedios
 */
public class MethodPropertyTest extends AbstractPropertyDescriptorTester {

	private static final String[] methodSignatures = new String[] {
		"String#indexOf(int)", 
		"String#substring(int,int)",
		"java.lang.String#substring(int,int)",
		"Integer#parseInt(String)",
		"java.util.HashMap#put(Object,Object)",
		"HashMap#containsKey(Object)"
		};	
	
	public MethodPropertyTest() {
	}

	@Test
    public void testAsStringOn() {
		
		Method method = null;
		
		for (int i=0; i<methodSignatures.length; i++) {
			method = MethodProperty.methodFrom(
					methodSignatures[i],
					MethodProperty.CLASS_METHOD_DELIMITER,
					MethodProperty.METHOD_ARG_DELIMITER
					);
			assertNotNull("Unable to identify method: " + methodSignatures[i], method);
			}
	}
	
	@Test
	public void testAsMethodOn() {
	
		Method[] methods = new Method[methodSignatures.length];
		
		for (int i=0; i<methodSignatures.length; i++) {
			methods[i] = MethodProperty.methodFrom(
					methodSignatures[i],
					MethodProperty.CLASS_METHOD_DELIMITER,
					MethodProperty.METHOD_ARG_DELIMITER
					);
			assertNotNull("Unable to identify method: " + methodSignatures[i], methods[i]);
			}
				
		String translatedMethod = null;
		for (int i=0; i<methods.length; i++) {
			translatedMethod = MethodProperty.asStringFor(methods[i]);
			assertTrue(
					"Translated method does not match",
					ClassUtil.withoutPackageName(methodSignatures[i]).equals(
							ClassUtil.withoutPackageName(translatedMethod))
					);
		}
	}
	
	@Override
	protected PropertyDescriptor createBadProperty(boolean multiValue) {
		
		Method[] methods = String.class.getDeclaredMethods();
		
		return multiValue ?
			new MethodMultiProperty("methodProperty", "asdf", new Method[] { methods[2], methods[3] }, new String[] { "java.util" } , 1.0f) :
			new MethodProperty("methodProperty", "asdf", methods[1], new String[] { "java.util" }, 1.0f); 
	}

	@Override
	protected Object createBadValue(int count) {
		
		Method[] allMethods = HashMap.class.getDeclaredMethods();
		
		if (count == 1) {
			return (Method)randomChoice(allMethods);
		}
		
		Method[] methods = new Method[count];
		for (int i=0; i<count; i++) {
			methods[i] = allMethods[i];
		}
		
		return methods;
	}

	@Override
	protected PropertyDescriptor createProperty(boolean multiValue) {

		Method[] methods = String.class.getDeclaredMethods();
		
		return multiValue ?
			new MethodMultiProperty("methodProperty", "asdf", new Method[] { methods[2], methods[3] }, new String[] { "java.lang" } , 1.0f) :
			new MethodProperty("methodProperty", "asdf", methods[1], new String[] { "java.lang" }, 1.0f); 
	}

	@Override
	protected Object createValue(int count) {
		
		Method[] allMethods = String.class.getDeclaredMethods();
		
		if (count == 1) {
			return (Method)randomChoice(allMethods);
		}
		
		Method[] methods = new Method[count];
		for (int i=0; i<count; i++) {
			methods[i] = allMethods[i];
		}
		
		return methods;
	}

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MethodPropertyTest.class);
    }
}
