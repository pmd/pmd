/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import net.sourceforge.pmd.lang.BaseLanguageModule;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class JspLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Java Server Pages";
    public static final String TERSE_NAME = "jsp";

    public JspLanguageModule() {
        super(NAME, "JSP", TERSE_NAME, "jsp", "jspx", "jspf", "tag");
        addVersion("", new JspHandler(), true);
    }
}
