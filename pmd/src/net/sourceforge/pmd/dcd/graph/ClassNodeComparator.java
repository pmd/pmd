/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.graph;

import java.util.Comparator;

/**
 * Compares ClassNodes by the name of the class.  Can also compare String class
 * names to ClassNodes.
 */
public final class ClassNodeComparator implements Comparator {

	public static final ClassNodeComparator INSTANCE = new ClassNodeComparator();

	private ClassNodeComparator() {
	}

	public int compare(Object obj1, Object obj2) {
		if (obj1 instanceof String && obj2 instanceof String) {
			return ((String)obj1).compareTo((String)obj2);
		} else if (obj1 instanceof String) {
			return ((String)obj1).compareTo(((ClassNode)obj2).getName());
		} else if (obj2 instanceof String) {
			return ((ClassNode)obj1).getName().compareTo((String)obj2);
		} else {
			return ((ClassNode)obj1).compareTo((ClassNode)obj2);
		}
	}
}
