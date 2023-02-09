/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.lang.jsp.JspLanguageModule;

public class JSPLanguage extends AbstractLanguage {
    public JSPLanguage() {
        super(JspLanguageModule.NAME, JspLanguageModule.TERSE_NAME, new JSPTokenizer(), JspLanguageModule.EXTENSIONS);
    }
}
