package net.sourceforge.pmd.util;

import java.math.BigDecimal;
import java.util.Map;


/**
 * Various class-related utility methods
 * 
 * @author Brian Remedios
 */
public class ClassUtil {

	private ClassUtil() {};
	
	private static final Map primitiveTypesByName = CollectionUtil.mapFrom( new Object[][] {
			{"int",		int.class },
			{"byte",	byte.class },
			{"long",	long.class },
			{"short",	short.class },
			{"float",	float.class },
			{"double",	double.class },
			{"char",	char.class },
			{"boolean", boolean.class },
			});
	
	private static final Map typesByShortName = CollectionUtil.mapFrom( new Object[][] {
			{"Integer",		Integer.class },
			{"Byte",		Byte.class },
			{"Long",		Long.class },
			{"Short",		Short.class },
			{"Float",		Float.class },
			{"Double",		Double.class },
			{"Character",	Character.class },
			{"Boolean", 	Boolean.class },
			{"BigDecimal",	BigDecimal.class },
			{"String",		String.class },
			{"Object",		Object.class },
			{"Object[]",	Object[].class }
			});
	
	/**
	 * Method getPrimitiveTypeFor.
	 * @param name String
	 * @return Class
	 */
	public static Class getPrimitiveTypeFor(String name) {
		return (Class)primitiveTypesByName.get(name);
	}
	
	/**
	 * Attempt to determine the actual class given the short name.
	 * 
	 * @param shortName String
	 * @return Class
	 */
	public static Class getTypeFor(String shortName) {
		
		Class cls = (Class)typesByShortName.get(shortName);
		if (cls != null) return cls;
		
		cls = (Class)primitiveTypesByName.get(shortName);
		if (cls != null) return cls;
		
		return CollectionUtil.getCollectionTypeFor(shortName);
	}
	
}
