package net.sourceforge.pmd.lang;

/**
 * Interface for registering XPath functions.
 */
public interface XPathFunctionRegister {

    XPathFunctionRegister DUMMY = new XPathFunctionRegister() {
	public void register() {
	}
    };

    /**
     * Register functions.
     */
    void register();
}
