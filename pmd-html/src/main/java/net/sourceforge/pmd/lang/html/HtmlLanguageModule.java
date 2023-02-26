/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.html.ast.HtmlTokenizer;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

public final class HtmlLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "HTML";
    static final String TERSE_NAME = "html";
    private static final HtmlLanguageModule INSTANCE = new HtmlLanguageModule();

    public HtmlLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                              .extensions("html", "htm", "xhtml", "xht", "shtml")
                              .addVersion("4")
                              .addDefaultVersion("5"),
              new HtmlHandler());
    }

    public static HtmlLanguageModule getInstance() {
        return INSTANCE;
    }

    @Override
    public Tokenizer createCpdTokenizer(LanguagePropertyBundle bundle) {
        return new HtmlTokenizer();
    }
}
