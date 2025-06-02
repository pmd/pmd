/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.html.cpd.HtmlCpdLexer;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

public final class HtmlLanguageModule extends SimpleLanguageModuleBase {
    static final String ID = "html";
    static final String NAME = "HTML";

    public HtmlLanguageModule() {
        super(LanguageMetadata.withId(ID).name(NAME)
                              .extensions("html", "htm", "xhtml", "xht", "shtml")
                              .addVersion("4")
                              .addDefaultVersion("5"),
              new HtmlHandler());
    }

    public static HtmlLanguageModule getInstance() {
        return (HtmlLanguageModule) LanguageRegistry.PMD.getLanguageById(ID);
    }

    @Override
    public CpdLexer createCpdLexer(LanguagePropertyBundle bundle) {
        return new HtmlCpdLexer();
    }
}
