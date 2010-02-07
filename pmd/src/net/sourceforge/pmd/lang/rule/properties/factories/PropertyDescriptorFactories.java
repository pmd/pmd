package net.sourceforge.pmd.lang.rule.properties.factories;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import com.sun.org.apache.bcel.internal.classfile.Method;

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
import net.sourceforge.pmd.util.StringUtil;
/**
 * 
 * @author Brian Remedios
 */
public class PropertyDescriptorFactories implements PropertyDescriptorFields {
	
	private static final Map<String, Boolean> numberFieldTypesByKey = BasicPropertyDescriptorFactory.expectedFieldTypesWith(
			new String[]  { minKey,  	  maxKey}, 
			new Boolean[] { Boolean.TRUE, Boolean.TRUE}
			);
	
	private static final Map<String, Boolean> packagedFieldTypesByKey = BasicPropertyDescriptorFactory.expectedFieldTypesWith(
			new String[]  { legalPackagesKey}, 
			new Boolean[] { Boolean.FALSE}
			);
	
	private static final PropertyDescriptorFactory stringPdf = new BasicPropertyDescriptorFactory<StringProperty>(String.class) {

		public StringProperty createWith(Map<String, String> valuesById) {
			return new StringProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					0f);
		}
	};
	
	private static final PropertyDescriptorFactory multiStringPdf = new BasicPropertyDescriptorFactory<StringMultiProperty>(String[].class) {

		public StringMultiProperty createWith(Map<String, String> valuesById) {
			final char delimiter = delimiterIn(valuesById);
			return new StringMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					StringUtil.substringsOf(defaultValueIn(valuesById), delimiter),
					0f,
					delimiter
					);
		}
	};
	
	private static final PropertyDescriptorFactory booleanPdf = new BasicPropertyDescriptorFactory<BooleanProperty>(Boolean.class) {

		public BooleanProperty createWith(Map<String, String> valuesById) {
			return new BooleanProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Boolean.valueOf(defaultValueIn(valuesById)),
					0f);
		}
	};
	
	private static final PropertyDescriptorFactory characterPdf = new BasicPropertyDescriptorFactory<CharacterProperty>(Character.class) {

		public CharacterProperty createWith(Map<String, String> valuesById) {
			return new CharacterProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					new Character(defaultValueIn(valuesById).charAt(0)),
					0f);
		}
	};
	
	private static final PropertyDescriptorFactory multiCharacterPdf = new BasicPropertyDescriptorFactory<CharacterMultiProperty>(Character[].class) {

		public CharacterMultiProperty createWith(Map<String, String> valuesById) {
			return new CharacterMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					null
					);
		}
	};
	
	private static final PropertyDescriptorFactory integerPdf = new BasicPropertyDescriptorFactory<IntegerProperty>(Integer.class, numberFieldTypesByKey) {

		public IntegerProperty createWith(Map<String, String> valuesById) {
			final String minMax[] = minMaxFrom(valuesById);		
			return new IntegerProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Integer.valueOf(minMax[0]),
					Integer.valueOf(minMax[1]),
					Integer.valueOf(numericDefaultValueIn(valuesById)),
					0f);
		}
	};

	private static final PropertyDescriptorFactory multiIntegerPdf = new BasicPropertyDescriptorFactory<IntegerMultiProperty>(Integer[].class, numberFieldTypesByKey) {

		public IntegerMultiProperty createWith(Map<String, String> valuesById) {
			final String[] minMax = minMaxFrom(valuesById);
			Integer[] defaultValues = integersIn(defaultValueIn(valuesById));
			return new IntegerMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Integer.parseInt(minMax[0]),
					Integer.parseInt(minMax[1]),
					defaultValues,
					0f
					);
		};
	};
		
	private static final PropertyDescriptorFactory longPdf = new BasicPropertyDescriptorFactory<LongProperty>(Long.class, numberFieldTypesByKey) {

		public LongProperty createWith(Map<String, String> valuesById) {
			final String minMax[] = minMaxFrom(valuesById);		
			return new LongProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Long.valueOf(minMax[0]),
					Long.valueOf(minMax[1]),
					Long.valueOf(numericDefaultValueIn(valuesById)),
					0f);
		}
	};

	private static final PropertyDescriptorFactory multiLongPdf = new BasicPropertyDescriptorFactory<LongMultiProperty>(Long[].class, numberFieldTypesByKey) {

		public LongMultiProperty createWith(Map<String, String> valuesById) {
			final String[] minMax = minMaxFrom(valuesById);
			Long[] defaultValues = longsIn(defaultValueIn(valuesById));
			return new LongMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Long.parseLong(minMax[0]),
					Long.parseLong(minMax[1]),
					defaultValues,
					0f
					);
		};
	};
	
	private static final PropertyDescriptorFactory floatPdf = new BasicPropertyDescriptorFactory<FloatProperty>(float.class, numberFieldTypesByKey) {

		public FloatProperty createWith(Map<String, String> valuesById) {
			final String minMax[] = minMaxFrom(valuesById);
			return new FloatProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Float.valueOf(minMax[0]),
					Float.valueOf(minMax[1]),
					Float.valueOf(numericDefaultValueIn(valuesById)),
					0f);
		}
	};
	
	private static final PropertyDescriptorFactory multiFloatPdf = new BasicPropertyDescriptorFactory<FloatMultiProperty>(Float[].class, numberFieldTypesByKey) {

		public FloatMultiProperty createWith(Map<String, String> valuesById) {
			final String[] minMax = minMaxFrom(valuesById);
			Float[] defaultValues = floatsIn(defaultValueIn(valuesById));
			return new FloatMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Float.parseFloat(minMax[0]),
					Float.parseFloat(minMax[1]),
					defaultValues,
					0f
					);
		};
	};
	
	private static final PropertyDescriptorFactory doublePdf = new BasicPropertyDescriptorFactory<DoubleProperty>(Double.class, numberFieldTypesByKey) {

		public DoubleProperty createWith(Map<String, String> valuesById) {
			final String minMax[] = minMaxFrom(valuesById);
			return new DoubleProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Double.valueOf(minMax[0]),
					Double.valueOf(minMax[1]),
					Double.valueOf(numericDefaultValueIn(valuesById)),
					0f);
		}
	};

	private static final PropertyDescriptorFactory multiDoublePdf = new BasicPropertyDescriptorFactory<DoubleMultiProperty>(Double[].class, numberFieldTypesByKey) {

		public DoubleMultiProperty createWith(Map<String, String> valuesById) {
			final String[] minMax = minMaxFrom(valuesById);
			Double[] defaultValues = doublesIn(defaultValueIn(valuesById));
			return new DoubleMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					Double.parseDouble(minMax[0]),
					Double.parseDouble(minMax[1]),
					defaultValues,
					0f
					);
		};
	};
	
	private static final PropertyDescriptorFactory enumPdf = new BasicPropertyDescriptorFactory<EnumeratedProperty>(Enumeration.class) {

		public EnumeratedProperty createWith(Map<String, String> valuesById) {

			return new EnumeratedProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					labelsIn(valuesById),
					choicesIn(valuesById),
					indexIn(valuesById),
					0f
					);
		}
	};

	private static final PropertyDescriptorFactory multiEnumPdf = new BasicPropertyDescriptorFactory<EnumeratedMultiProperty>(Enumeration[].class) {

		public EnumeratedMultiProperty createWith(Map<String, String> valuesById) {

			return new EnumeratedMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					labelsIn(valuesById),
					choicesIn(valuesById),
					indiciesIn(valuesById),
					0f
					);
		}
	};
	
	private static final PropertyDescriptorFactory typePdf = new BasicPropertyDescriptorFactory<TypeProperty>(Class.class, packagedFieldTypesByKey) {

		public TypeProperty createWith(Map<String, String> valuesById) {
			return new TypeProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					legalPackageNamesIn(valuesById),
					0f);
		}
	};

	private static final PropertyDescriptorFactory multiTypePdf = new BasicPropertyDescriptorFactory<TypeMultiProperty>(Class[].class, packagedFieldTypesByKey) {
		
		public TypeMultiProperty createWith(Map<String, String> valuesById) {
			return new TypeMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					legalPackageNamesIn(valuesById),
					0f);
		}
	};
	
	private static final PropertyDescriptorFactory methodPdf = new BasicPropertyDescriptorFactory<MethodProperty>(Method.class, packagedFieldTypesByKey) {

		public MethodProperty createWith(Map<String, String> valuesById) {
			return new MethodProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					legalPackageNamesIn(valuesById),
					0f);
		}
	};

	private static final PropertyDescriptorFactory multiMethodPdf = new BasicPropertyDescriptorFactory<MethodMultiProperty>(Method[].class, packagedFieldTypesByKey) {

		public MethodMultiProperty createWith(Map<String, String> valuesById) {
			return new MethodMultiProperty(
					nameIn(valuesById),
					descriptionIn(valuesById),
					defaultValueIn(valuesById),
					legalPackageNamesIn(valuesById),
					0f);
		}
	};
	
    private static final Map<String, PropertyDescriptorFactory> descriptorFactoriesByType;
    static {
    	Map<String, PropertyDescriptorFactory> temp = new HashMap<String, PropertyDescriptorFactory>(18);
    	
    	temp.put("Boolean", 	booleanPdf);
    	
    	temp.put("String", 		stringPdf);
    	temp.put("String[]", 	multiStringPdf);
    	temp.put("Character", 	characterPdf);
    	temp.put("Character[]", multiCharacterPdf);
    	
    	temp.put("Integer", 	integerPdf);
    	temp.put("Integer[]", 	multiIntegerPdf);
    	temp.put("Long", 		longPdf);
    	temp.put("Long[]", 		multiLongPdf);
    	temp.put("Float", 		floatPdf);
    	temp.put("Float[]", 	multiFloatPdf);
    	temp.put("Double", 		doublePdf);
    	temp.put("Double[]", 	multiDoublePdf);
    	
    	temp.put("Enum", 		enumPdf);
    	temp.put("Enum[]", 		multiEnumPdf);
    	
    	temp.put("Class", 		typePdf);
    	temp.put("Class[]", 	multiTypePdf);
    	temp.put("Method", 		methodPdf);
    	temp.put("Method[]", 	multiMethodPdf);
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
