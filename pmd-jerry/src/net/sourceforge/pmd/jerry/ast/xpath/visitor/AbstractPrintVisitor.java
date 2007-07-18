package net.sourceforge.pmd.jerry.ast.xpath.visitor;

import net.sourceforge.pmd.jerry.ast.xpath.Node;

public abstract class AbstractPrintVisitor {

	public static final String EOL = System.getProperty("line.separator");

	protected int indentLevel;

	protected final StringBuffer outputBuffer = new StringBuffer();

	protected final StringBuffer lineBuffer = new StringBuffer();

	public String getOutput() {
		flush();
		return outputBuffer.toString();
	}

	protected void print(String s) {
		lineBuffer.append(s);
	}

	protected void println(String s) {
		print(s);
		println();
	}

	protected void println() {
		print(EOL);
		flush();
		applyIndent();
	}

	protected void flush() {
		boolean append = false;
		for (int i = 0; i < lineBuffer.length(); i++) {
			if (lineBuffer.charAt(i) != '\t') {
				append = true;
				break;
			}
		}
		if (append) {
			outputBuffer.append(lineBuffer);
		}
		lineBuffer.setLength(0);
	}

	protected void applyIndent() {
		flush();
		for (int i = 0; i < indentLevel; i++) {
			lineBuffer.append('\t');
		}
	}

	protected void incrementIndent() {
		indentLevel++;
		applyIndent();
	}

	protected void decrementIndent() {
		indentLevel--;
		applyIndent();
	}

	protected void TODO(Node node) {
		String nodeName = node.getClass().getName();
		StringBuffer buf = new StringBuffer(100);
		buf.append("Visit for ");
		buf.append(node);
		if (node.jjtGetParent() != null) {
			buf.append(" w/ parent ");
			buf.append(node.jjtGetParent());
		}
		System.out.println(buf);
	}

}
