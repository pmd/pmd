/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;

/**
 * Indicates a problem during the execution of a custom
 * XPath function.
 */
public class XPathFunctionException extends Exception {
    public XPathFunctionException(String message) {
        super(message);
    }

    public XPathFunctionException(String message, Throwable cause) {
        super(message, cause);
    }
}
