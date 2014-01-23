/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.asm;

public class PrintVisitor {

	private static final String INDENT = "\t";

	private final int level;

	public PrintVisitor() {
		this(0);
	}

	public PrintVisitor(PrintVisitor parent) {
		this(parent.level + 2);
	}

	public PrintVisitor(int level) {
		this.level = level;
	}

	public void println(String s) {
		println(this.level, s);
	}

	public void printlnIndent(String s) {
		println(this.level + 1, s);
	}

	private void println(int level, String s) {
		for (int i = 0; i < level; i++) {
			System.out.print(INDENT);
		}
		System.out.println(s);
	}
}
