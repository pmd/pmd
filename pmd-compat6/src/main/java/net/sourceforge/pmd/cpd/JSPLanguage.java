/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This file has been taken from 6.55.0

package net.sourceforge.pmd.cpd;

public class JSPLanguage extends AbstractLanguage {
    public JSPLanguage() {
        super("JSP", "jsp", new JSPTokenizer(), ".jsp", ".jspx", ".jspf", ".tag");
    }
}
