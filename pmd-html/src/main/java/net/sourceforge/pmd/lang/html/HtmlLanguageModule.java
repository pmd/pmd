/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.processor.SimpleBatchLanguageProcessor;

public final class HtmlLanguageModule extends BaseLanguageModule {

    public static final String NAME = "HTML";
    public static final String TERSE_NAME = "html";

    public HtmlLanguageModule() {
        super(NAME, null, TERSE_NAME, "html", "htm", "xhtml", "xht", "shtml");
        addDefaultVersion("", new HtmlHandler());
    }

    @Override
    public LanguageProcessor createProcessor(LanguagePropertyBundle bundle) {
        return new SimpleBatchLanguageProcessor(bundle, new HtmlHandler());
    }

}
