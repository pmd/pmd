/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.properties.factories;

import static net.sourceforge.pmd.PropertyDescriptorFields.DEFAULT_VALUE;
import static net.sourceforge.pmd.PropertyDescriptorFields.DELIMITER;
import static net.sourceforge.pmd.PropertyDescriptorFields.DESC;
import static net.sourceforge.pmd.PropertyDescriptorFields.LEGAL_PACKAGES;
import static net.sourceforge.pmd.PropertyDescriptorFields.MAX;
import static net.sourceforge.pmd.PropertyDescriptorFields.MIN;
import static net.sourceforge.pmd.PropertyDescriptorFields.NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.PropertyDescriptorFactory;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;

/**
 * 
 * @author Brian Remedios
 *
 * @param <T>
 */
public class BasicPropertyDescriptorFactory<T> implements PropertyDescriptorFactory {

	private final Class<?> valueType;
	private final Map<String, Boolean> fieldTypesByKey;

	protected static final Map<String, Boolean> coreFieldTypesByKey = CollectionUtil.mapFrom(
			new String[]  { NAME, 		DESC, 	DEFAULT_VALUE}, 
			new Boolean[] { Boolean.TRUE,  Boolean.TRUE, Boolean.TRUE}
			);
	
	public BasicPropertyDescriptorFactory(Class<?> theValueType) {
		valueType = theValueType;
		fieldTypesByKey = Collections.unmodifiableMap(coreFieldTypesByKey);
	}
	
//	public interface WrapperBuilder<T> {
//		T[] newArray(int size);
//		T itemFrom(String txt);
//	}
//	
//	protected WrapperBuilder intBuilder = new WrapperBuilder<Integer>() {
//		public Integer[] newArray(int size) { return new Integer[size]; }
//		public Integer itemFrom(String txt) { return Integer.parseInt(txt); }
//	};
	
	public BasicPropertyDescriptorFactory(Class<?> theValueType, Map<String, Boolean> additionalFieldTypesByKey) {
		
		valueType = theValueType;
		Map<String, Boolean> temp = new HashMap<String, Boolean>(coreFieldTypesByKey.size() + additionalFieldTypesByKey.size());
		temp.putAll(coreFieldTypesByKey);
		temp.putAll(additionalFieldTypesByKey);
		
		fieldTypesByKey = Collections.unmodifiableMap(temp);
	}
	
	public Class<?> valueType() {
		return valueType;
	}
	
	public PropertyDescriptor<?> createWith(Map<String, String> valuesById) {
		throw new RuntimeException("Unimplemented createWith() method in subclass");
	}

	public Map<String, Boolean> expectedFields() {
		return fieldTypesByKey;
	}

	protected String nameIn(Map<String, String> valuesById) {
		return valuesById.get(NAME);
	}
	
	protected String descriptionIn(Map<String, String> valuesById) {
		return valuesById.get(DESC);
	}
	
	protected String defaultValueIn(Map<String, String> valuesById) {
		return valuesById.get(DEFAULT_VALUE);
	}
	
	protected String numericDefaultValueIn(Map<String, String> valuesById) {
		String number = defaultValueIn(valuesById);
		return StringUtil.isEmpty(number) ? "0" : number;	// TODO is 0 reasonable if undefined?
	}
	
	protected static String minValueIn(Map<String, String> valuesById) {
		return valuesById.get(MIN);
	}
	
	protected static String maxValueIn(Map<String, String> valuesById) {
		return valuesById.get(MAX);
	}

//	protected static T[] primitivesFrom(String text, WrapperBuilder<T> builder) {
//		
//		String[] values = text.split(",");	// TODO
//		List items = new ArrayList(values.length);
//		for (String value : values) {
//			try {
//				Object newIten = builder.itemFrom(value);
//				items.add(newIten);
//			} catch (Exception ex) {
//				
//			}
//		}
//		return items.toArray(builder.newArray(items.size()));
//	}
	
	protected static Integer[] integersIn(String numberString) {
		String[] values = numberString.split(",");	// TODO
		List<Integer> ints = new ArrayList<Integer>(values.length);
		for (String value : values) {
			try {
				Integer newInt = Integer.parseInt(value);
				ints.add(newInt);
			} catch (Exception ex) {
				
			}
		}
		return ints.toArray(new Integer[ints.size()]);
	}
	
	protected static Long[] longsIn(String numberString) {
		String[] values = numberString.split(",");	// TODO
		List<Long> longs = new ArrayList<Long>(values.length);
		for (String value : values) {
			try {
				Long newLong = Long.parseLong(value);
				longs.add(newLong);
			} catch (Exception ex) {
				
			}
		}
		return longs.toArray(new Long[longs.size()]);
	}
	
	protected static Float[] floatsIn(String numberString) {
		String[] values = numberString.split(",");	// TODO
		List<Float> floats = new ArrayList<Float>(values.length);
		for (String value : values) {
			try {
				Float newFloat = Float.parseFloat(value);
				floats.add(newFloat);
			} catch (Exception ex) {
				
			}
		}
		return floats.toArray(new Float[floats.size()]);
	}
	
	protected static Double[] doublesIn(String numberString) {
		String[] values = numberString.split(",");	// TODO
		List<Double> doubles = new ArrayList<Double>(values.length);
		for (String value : values) {
			try {
				Double newDouble = Double.parseDouble(value);
				doubles.add(newDouble);
			} catch (Exception ex) {
				
			}
		}
		return doubles.toArray(new Double[doubles.size()]);
	}
	
	protected static String[] labelsIn(Map<String, String> valuesById) {
		return null;	// TODO
	}
	
	protected static Object[] choicesIn(Map<String, String> valuesById) {
		return null;	// TODO
	}
	
	protected static int indexIn(Map<String, String> valuesById) {
		return 0;	// TODO
	}
	
	protected static int[] indiciesIn(Map<String, String> valuesById) {
		return null;	// TODO
	}	

	protected static char delimiterIn(Map<String, String> valuesById) {
		String characterStr = valuesById.get(DELIMITER).trim();
		return characterStr.charAt(0);
	}
	
	protected static String[] minMaxFrom(Map<String, String> valuesById) {
		String min = minValueIn(valuesById);
		String max = maxValueIn(valuesById);
		if (StringUtil.isEmpty(min) || StringUtil.isEmpty(max)) {
			throw new RuntimeException("min and max values must be specified");
		}
		return new String[] { min, max };
	}
		
	protected static String[] legalPackageNamesIn(Map<String, String> valuesById) {
		String names = valuesById.get(LEGAL_PACKAGES);
		if (StringUtil.isEmpty(names)) {
			return null;
		}
		return StringUtil.substringsOf(names, '|');	// TODO - get const
	}
	
	public static Map<String, Boolean> expectedFieldTypesWith(String[] otherKeys, Boolean[] otherValues) {
		Map<String, Boolean> largerMap = new HashMap<String, Boolean>(otherKeys.length + coreFieldTypesByKey.size());
		largerMap.putAll(coreFieldTypesByKey);
		for (int i=0; i<otherKeys.length; i++) {
			largerMap.put(otherKeys[i], otherValues[i]);
		}
		return largerMap;
	}
	
//	protected static Map<String, PropertyDescriptorFactory> factoriesByTypeIdFrom(PropertyDescriptorFactory[] factories) {
//		Map<String, PropertyDescriptorFactory> factoryMap = new HashMap<String, PropertyDescriptorFactory>(factories.length);
//		for (PropertyDescriptorFactory factory : factories) factoryMap.put(factory.typeId(), factory);
//		return factoryMap;
//	}
//	
}
