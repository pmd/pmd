/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ast;

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

	public void testGeneric() {
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
