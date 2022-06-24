/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html;

import net.sourceforge.pmd.lang.BaseLanguageModule;

public final class HtmlLanguageModule extends BaseLanguageModule {

    public static final String NAME = "HTML";
    public static final String TERSE_NAME = "html";

    public HtmlLanguageModule() {
        super(NAME, null, TERSE_NAME, "html", "htm", "xhtml", "xht", "shtml");
        addDefaultVersion("", new HtmlHandler());
    }

}
