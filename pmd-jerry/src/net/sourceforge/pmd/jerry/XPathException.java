package net.sourceforge.pmd.jerry;

public class XPathException extends Exception {
	public XPathException() {
		super();
	}

	public XPathException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public XPathException(String message) {
		super(message);
	}

	public XPathException(Throwable throwable) {
		super(throwable);
	}
}
