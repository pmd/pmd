/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * This file is to test the JavaCC java grammer, whether we can parse specific java constructs without
 * throwing a syntax error.
 */

class Superclass {

	public Superclass() {
	}

	public <V> Superclass(Class<V> clazz) {
	}

	<T> T doStuff(T s) {
		return s;
	}
}

class Outer {
	Outer() {
		System.out.println("Outer constructor");
	}
	
	class Inner {
		Inner() {
			System.out.println("Inner constructor");
		}
	}
}
class Child extends Outer.Inner {
	Child(Outer o) {
		o.super();
		System.out.println("Child constructor");
	}
}

public class ParserCornerCases extends Superclass {

	public ParserCornerCases() {
		super();
	}

	public ParserCornerCases(int a) {
		<Integer> this(a, 2);
	}

	public <W> ParserCornerCases(int a, int b) {
		<String> super(String.class);
	}

	public ParserCornerCases(String title) {
		this();
	}

	public strictfp void testGeneric() {
		String o = super.<String> doStuff("foo");
		String v = this.<String> thisGeneric("bar");
	}

	<X> X thisGeneric(X x) {
		return x;
	}

	Class getByteArrayClass() {
		return (byte[].class);
	}
}

/**
 * Test case from http://jira.codehaus.org/browse/MPMD-126
 */
class PmdTestParent {
	public PmdTestParent(Object obj) {}
}

class PmdTestChild extends PmdTestParent {

	public PmdTestChild() {
		// the following line produced a parsing problem
		super(new Object() {

			public Object create() {

				Object memoryMonitor = null;

				if (memoryMonitor == null) {
					memoryMonitor = new Object();
				}

				return memoryMonitor;
			}
		});
	}
}