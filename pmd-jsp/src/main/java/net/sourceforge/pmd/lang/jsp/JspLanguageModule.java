/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.jsp.cpd.JspCpdLexer;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class JspLanguageModule extends SimpleLanguageModuleBase implements CpdCapableLanguage {
    private static final String ID = "jsp";


    public JspLanguageModule() {
        super(LanguageMetadata.withId(ID).name("Java Server Pages").shortName("JSP")
                              .extensions("jsp", "jspx", "jspf", "tag")
                              .addVersion("2")
                              .addDefaultVersion("3"),
              new JspHandler());
    }

    public static JspLanguageModule getInstance() {
        return (JspLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new JspCpdLexer();
    }
}
