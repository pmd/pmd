package net.sourceforge.pmd.util;

import java.math.BigDecimal;


/**
 * Various class-related utility methods
 * 
 * @author Brian Remedios
 */
public class ClassUtil {

	private ClassUtil() {};
	
	private static final TypeMap primitiveTypesByName = new TypeMap( new Class[] {
			int.class,
			byte.class,
			long.class,
			short.class,
			float.class,
			double.class,
			char.class,
			boolean.class,
			});
	
	private static final TypeMap typesByNames = new TypeMap( new Class[] {
			Integer.class,
			Byte.class,
			Long.class,
			Short.class,
			Float.class,
			Double.class,
			Character.class,
			Boolean.class,
			BigDecimal.class,
			String.class,
			Object.class,
			});
	
	/**
	 * Returns the type(class) for the name specified
	 * or null if not found.
	 * 
	 * @param name String
	 * @return Class
	 */
	public static Class getPrimitiveTypeFor(String name) {
		return primitiveTypesByName.typeFor(name);
	}
	
	/**
	 * Attempt to determine the actual class given the short name.
	 * 
	 * @param shortName String
	 * @return Class
	 */
	public static Class getTypeFor(String shortName) {
		
		Class type = typesByNames.typeFor(shortName);
		if (type != null) return type;
		
		type = primitiveTypesByName.typeFor(shortName);
		if (type != null) return type;
		
		return CollectionUtil.getCollectionTypeFor(shortName);
	}
	/**
	 * Returns the abbreviated name of the type,
	 * without the package name
	 * 
	 * @param fullTypeName
	 * @return String
	 */
			
	public static String withoutPackageName(String fullTypeName) {
		
		int dotPos = fullTypeName.lastIndexOf('.');
		
		return dotPos > 0 ?
			fullTypeName.substring(dotPos+1) :
			fullTypeName;
	}
}
