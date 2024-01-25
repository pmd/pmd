/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;

public class XPathFunctionException extends Exception {
    public XPathFunctionException(String message) {
        super(message);
    }

    public XPathFunctionException(String message, Throwable cause) {
        super(message, cause);
    }
}
