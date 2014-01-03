/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.typeresolution;

import java.util.Arrays;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.symboltable.TypedNameDeclaration;

public class TypeHelper {

	public static boolean isA(TypeNode n, Class<?> clazz) {
		return subclasses(n, clazz);
	}

	public static boolean isEither(TypeNode n, Class<?> class1, Class<?> class2) {
		return subclasses(n, class1) || subclasses(n, class2);
	}
	
	public static boolean isA(TypedNameDeclaration vnd, Class<?> clazz) {
		Class<?> type = vnd.getType();
		return type != null && type.equals(clazz) || type == null
		&& (clazz.getSimpleName().equals(vnd.getTypeImage()) || clazz.getName().equals(vnd.getTypeImage()));
	}

	public static boolean isEither(TypedNameDeclaration vnd, Class<?> class1, Class<?> class2) {
		return isA(vnd, class1) || isA(vnd, class2);
	}
	
	public static boolean isNeither(TypedNameDeclaration vnd, Class<?> class1, Class<?> class2) {
		return !isA(vnd, class1) &&  !isA(vnd, class2);
	}
	
	public static boolean subclasses(TypeNode n, Class<?> clazz) {
		Class<?> type = n.getType();
		if (type == null) {
			return clazz.getSimpleName().equals(((Node) n).getImage()) || clazz.getName().equals(((Node) n).getImage());
		}

		if (type.equals(clazz)) {
			return true;
		}

		List<Class<?>> implementors = Arrays.asList(type.getInterfaces());
		if (implementors.contains(clazz)) {
			return true;
		}
		Class<?> superC = type.getSuperclass();
		while (superC != null && !superC.equals(Object.class)) {
			if (superC.equals(clazz)) {
				return true;
			}
			superC = superC.getSuperclass();
		}
		return false;
	}
}
