/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import net.sourceforge.pmd.cpd.CpdCapableLanguage;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.jsp.cpd.JSPTokenizer;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class JspLanguageModule extends SimpleLanguageModuleBase implements CpdCapableLanguage {

    private static final JspLanguageModule INSTANCE = new JspLanguageModule();

    public JspLanguageModule() {
        super(LanguageMetadata.withId("jsp").name("Java Server Pages").shortName("JSP")
                              .extensions("jsp", "jspx", "jspf", "tag")
                              .addVersion("2")
                              .addDefaultVersion("3"),
              new JspHandler());
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new JSPTokenizer();
    }

    public static JspLanguageModule getInstance() {
        return INSTANCE;
    }
}
