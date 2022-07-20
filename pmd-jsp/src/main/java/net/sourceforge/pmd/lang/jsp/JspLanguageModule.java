/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.processor.SimpleBatchLanguageProcessor;

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

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new SimpleBatchLanguageProcessor(bundle, new JspHandler());
    }
}
