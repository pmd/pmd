/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ClassLoader utilities.  Useful for extracting additional details from a class
 * hierarchy beyond the basic standard Java Reflection APIs.
 */
public class ClassLoaderUtil {

    public static final String CLINIT = "<clinit>";

    public static final String INIT = "<init>";

    public static String fromInternalForm(String internalForm) {
	return internalForm.replace('/', '.');
    }

    public static String toInternalForm(String internalForm) {
	return internalForm.replace('.', '/');
    }

    public static Class<?> getClass(String name) {
	try {
	    return ClassLoaderUtil.class.getClassLoader().loadClass(name);
	} catch (ClassNotFoundException e) {
	    throw new RuntimeException(e);
	} catch (NoClassDefFoundError e) {
	    throw new RuntimeException(e);
	}
    }

    public static Field getField(Class<?> type, String name) {
	try {
	    return myGetField(type, name);
	} catch (NoSuchFieldException e) {
	    throw new RuntimeException(e);
	}
    }

    private static Field myGetField(Class<?> type, String name) throws NoSuchFieldException {
	// Scan the type hierarchy just like Class.getField(String) using
	// Class.getDeclaredField(String)
	try {
	    return type.getDeclaredField(name);
	} catch (NoSuchFieldException e) {
	    // Try the super interfaces
	    for (Class<?> superInterface : type.getInterfaces()) {
		try {
		    return myGetField(superInterface, name);
		} catch (NoSuchFieldException e2) {
		    // Okay
		}
	    }
	    // Try the super classes
	    if (type.getSuperclass() != null) {
		return myGetField(type.getSuperclass(), name);
	    } else {
		throw new NoSuchFieldException(type.getName() + "." + name);
	    }
	}
    }

    public static Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
	try {
	    return myGetMethod(type, name, parameterTypes);
	} catch (NoSuchMethodException e) {
	    throw new RuntimeException(e);
	}
    }

    private static Method myGetMethod(Class<?> type, String name, Class<?>... parameterTypes)
	    throws NoSuchMethodException {
	// Scan the type hierarchy just like Class.getMethod(String, Class[])
	// using Class.getDeclaredMethod(String, Class[])
	// System.out.println("type: " + type);
	// System.out.println("name: " + name);
	// System.out
	// .println("parameterTypes: " + Arrays.toString(parameterTypes));
	try {
	    // System.out.println("Checking getDeclaredMethod");
	    // for (Method m : type.getDeclaredMethods()) {
	    // System.out.println("\t" + m);
	    // }
	    return type.getDeclaredMethod(name, parameterTypes);
	} catch (NoSuchMethodException e) {
	    try {
		// Try the super classes
		if (type.getSuperclass() != null) {
		    // System.out.println("Checking super: "
		    // + type.getSuperclass());
		    return myGetMethod(type.getSuperclass(), name, parameterTypes);
		}
	    } catch (NoSuchMethodException e2) {
		// Okay
	    }
	    // Try the super interfaces
	    for (Class<?> superInterface : type.getInterfaces()) {
		try {
		    // System.out.println("Checking super interface: "
		    // + superInterface);
		    return myGetMethod(superInterface, name, parameterTypes);
		} catch (NoSuchMethodException e3) {
		    // Okay
		}
	    }
	    throw new NoSuchMethodException(type.getName() + '.' + getMethodSignature(name, parameterTypes));
	}
    }

    public static Constructor<?> getConstructor(Class<?> type, String name, Class<?>... parameterTypes) {
	try {
	    return type.getDeclaredConstructor(parameterTypes);
	} catch (NoSuchMethodException e) {
	    throw new RuntimeException(e);
	}
    }

    public static String getMethodSignature(String name, Class<?>... parameterTypes) {
	StringBuilder builder = new StringBuilder(name);
	if (!(name.equals(CLINIT) || name.equals(INIT))) {
	    builder.append('(');
	    if (parameterTypes != null && parameterTypes.length > 0) {
	    	builder.append(parameterTypes[0].getName());
	    	for (int i = 1; i < parameterTypes.length; i++) {
	    		builder.append(", ").append(parameterTypes[i].getName());
	    	}
	    }
	    builder.append(')');
	}
	return builder.toString();
    }

    public static Class<?>[] getParameterTypes(String... parameterTypeNames) {
	Class<?>[] parameterTypes = new Class[parameterTypeNames.length];
	for (int i = 0; i < parameterTypeNames.length; i++) {
	    parameterTypes[i] = getClass(parameterTypeNames[i]);
	}
	return parameterTypes;
    }

    public static boolean isOverridenMethod(Class<?> clazz, Method method, boolean checkThisClass) {
	try {
	    if (checkThisClass) {
		clazz.getDeclaredMethod(method.getName(), method.getParameterTypes());
		return true;
	    }
	} catch (NoSuchMethodException e) {
	}
	// Check super class
	if (clazz.getSuperclass() != null) {
	    if (isOverridenMethod(clazz.getSuperclass(), method, true)) {
		return true;
	    }
	}
	// Check interfaces
	for (Class<?> anInterface : clazz.getInterfaces()) {
	    if (isOverridenMethod(anInterface, method, true)) {
		return true;
	    }
	}
	return false;
    }
}
