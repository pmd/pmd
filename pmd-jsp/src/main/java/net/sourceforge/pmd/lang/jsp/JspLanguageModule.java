/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class JspLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "Java Server Pages";
    public static final String TERSE_NAME = "jsp";

    public JspLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).shortName("JSP")
                              .extensions("jsp", "jspx", "jspf", "tag"),
              new JspHandler());
    }

}
