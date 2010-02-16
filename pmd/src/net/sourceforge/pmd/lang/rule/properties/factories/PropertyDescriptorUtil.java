package net.sourceforge.pmd.lang.rule.properties.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.PropertyDescriptorFields;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.CharacterMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.CharacterProperty;
import net.sourceforge.pmd.lang.rule.properties.DoubleMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.DoubleProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.FloatMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.FloatProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;
import net.sourceforge.pmd.lang.rule.properties.LongMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.LongProperty;
import net.sourceforge.pmd.lang.rule.properties.MethodMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.MethodProperty;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.lang.rule.properties.TypeMultiProperty;
import net.sourceforge.pmd.lang.rule.properties.TypeProperty;
/**
 * 
 * @author Brian Remedios
 */
public class PropertyDescriptorUtil implements PropertyDescriptorFields {
						
    private static final Map<String, PropertyDescriptorFactory> descriptorFactoriesByType;
    static {
    	Map<String, PropertyDescriptorFactory> temp = new HashMap<String, PropertyDescriptorFactory>(18);
    	
    	temp.put("Boolean", 	BooleanProperty.factory);
    	
    	temp.put("String", 		StringProperty.factory);
    	temp.put("String[]", 	StringMultiProperty.factory);
    	temp.put("Character", 	CharacterProperty.factory);
    	temp.put("Character[]", CharacterMultiProperty.factory);
    	
    	temp.put("Integer", 	IntegerProperty.factory);
    	temp.put("Integer[]", 	IntegerMultiProperty.factory);
    	temp.put("Long", 		LongProperty.factory);
    	temp.put("Long[]", 		LongMultiProperty.factory);
    	temp.put("Float", 		FloatProperty.factory);
    	temp.put("Float[]", 	FloatMultiProperty.factory);
    	temp.put("Double", 		DoubleProperty.factory);
    	temp.put("Double[]", 	DoubleMultiProperty.factory);
    	
    	temp.put("Enum", 		EnumeratedProperty.factory);
    	temp.put("Enum[]", 		EnumeratedMultiProperty.factory);
    	
    	temp.put("Class", 		TypeProperty.factory);
    	temp.put("Class[]", 	TypeMultiProperty.factory);
    	temp.put("Method", 		MethodProperty.factory);
    	temp.put("Method[]", 	MethodMultiProperty.factory);
    	descriptorFactoriesByType = Collections.unmodifiableMap(temp);
    	}
    
    public static PropertyDescriptorFactory factoryFor(String typeId) {
    	return descriptorFactoriesByType.get(typeId);
    }
    
    public static String typeIdFor(Class<?> valueType) {
    	
    	// a reverse lookup, not very efficient but fine for now
    	for (Map.Entry<String, PropertyDescriptorFactory> entry : descriptorFactoriesByType.entrySet()) {
    		if (entry.getValue().valueType() == valueType) {
    			return entry.getKey();
    		}
    	}
    	return null;
    }
}
