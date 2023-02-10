/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

public final class HtmlLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "HTML";
    public static final String TERSE_NAME = "html";

    public HtmlLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                              .extensions("html", "htm", "xhtml", "xht", "shtml"),
              new HtmlHandler());
    }

    public static HtmlLanguageModule getInstance() {
        return (HtmlLanguageModule) LanguageRegistry.PMD.getLanguageById(TERSE_NAME);
    }
}
