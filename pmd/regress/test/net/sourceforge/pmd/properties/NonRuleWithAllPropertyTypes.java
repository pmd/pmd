package test.net.sourceforge.pmd.properties;

import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.CharacterMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.CharacterProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.FloatMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.FloatProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.lang.rule.properties.TypeMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.TypeProperty;

/**
 * 
 * @author Brian Remedios
 */
class NonRuleWithAllPropertyTypes extends AbstractJavaRule {

	// descriptors are public to enable us to write external tests
	public static final StringProperty singleStr	= new StringProperty("singleStr", "Property with a single string value", "hello world" , 3.0f);
	public static final StringMultiProperty multiStr		= new StringMultiProperty("multiStr", "Property with multiple string values", new String[] {"hello", "world"}, 5.0f, '|');
	
	public static final IntegerProperty singleInt	= new IntegerProperty("singleInt", "Property with a single integer value", 1, 10, 8 , 3.0f);
	public static final IntegerMultiProperty multiInt		= new IntegerMultiProperty("multiInt", "Property with multiple integer values", 0, 10, new Integer[] {1,2,3,4}, 5.0f);
	
	public static final BooleanProperty singleBool	= new BooleanProperty("singleBool", "Property with a single boolean value", true, 6.0f);
	public static final BooleanMultiProperty multiBool	= new BooleanMultiProperty("multiBool", "Property with multiple boolean values", new Boolean[] { true, false}, 5.0f);
	
	public static final CharacterProperty singleChar	= new CharacterProperty("singleChar", "Property with a single character value", 'a', 5.0f);
	public static final CharacterMultiProperty multiChar	= new CharacterMultiProperty("multiChar", "Property with multiple character values", new Character[] {'a', 'e', 'i', 'o', 'u'}, 6.0f, '|');
	
	public static final FloatProperty singleFloat	= new FloatProperty("singleFloat", "Property with a single float value", 9f, 10f, .9f, 5.0f);
	public static final FloatMultiProperty multiFloat	= new FloatMultiProperty("multiFloat", "Property with multiple float values", 0f, 5f, new Float[] {1f,2f,3f}, 6.0f);
	
	public static final TypeProperty singleType	= new TypeProperty("singleType", "Property with a single type value", String.class, new String[] { "java.lang" }, 5.0f);
	public static final TypeMultiProperty multiType	= new TypeMultiProperty("multiType", "Property with multiple type values", new Class[] {Integer.class, Object.class}, new String[] { "java.lang" }, 6.0f);

	public static final EnumeratedProperty<Class> enumType			= new EnumeratedProperty<Class>("enumType", "Property with a enumerated choices", new String[] {"String", "Object"}, new Class[] {String.class, Object.class}, 1, 5.0f);
	public static final EnumeratedMultiProperty<Class> multiEnumType	= new EnumeratedMultiProperty<Class>("multiEnumType", "Property with a enumerated choices", new String[] {"String", "Object"}, new Class[] {String.class, Object.class}, new int[] {0,1}, 5.0f);
	
	
	public NonRuleWithAllPropertyTypes() {
		super();
		definePropertyDescriptor(singleStr);
		definePropertyDescriptor(multiStr);
		definePropertyDescriptor(singleInt);
		definePropertyDescriptor(multiInt);
		definePropertyDescriptor(singleBool);
		definePropertyDescriptor(multiBool);
		definePropertyDescriptor(singleChar);
		definePropertyDescriptor(multiChar);
		definePropertyDescriptor(singleFloat);
		definePropertyDescriptor(multiFloat);
		definePropertyDescriptor(singleType);
		definePropertyDescriptor(multiType);
		definePropertyDescriptor(enumType);
		definePropertyDescriptor(multiEnumType);
	}
}
