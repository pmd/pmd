package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.lang.xpath.Initializer;

import org.jaxen.Navigator;

/**
 * Interface for performing Language specific XPath handling, such as
 * initialization and navigation.
 */
public interface XPathHandler {

    XPathHandler DUMMY = new XPathHandler() {
	public void initialize() {
	}

	public Navigator getNavigator() {
	    return null;
	}
    };

    /**
     * Initialize.  This is intended to be called by {@link Initializer} to
     * perform Language specific initialization.
     */
    void initialize();

    /**
     * Get a Jaxen Navigator for this Language.  May return <code>null</code>
     * if there is no Jaxen Navigation for this language.
     */
    Navigator getNavigator();
}
