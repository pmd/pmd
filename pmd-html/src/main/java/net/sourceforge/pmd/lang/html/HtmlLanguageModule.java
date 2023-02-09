/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

public final class HtmlLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "HTML";
    public static final String TERSE_NAME = "html";
    @InternalApi
    public static final List<String> EXTENSIONS = listOf("html", "htm", "xhtml", "xht", "shtml");

    public HtmlLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME)
                              .extensions(EXTENSIONS.get(0), EXTENSIONS.subList(0, EXTENSIONS.size()).toArray(new String[0]))
                              .addVersion("4")
                              .addDefaultVersion("5"),
              new HtmlHandler());
    }

    public static HtmlLanguageModule getInstance() {
        return (HtmlLanguageModule) LanguageRegistry.PMD.getLanguageById(TERSE_NAME);
    }
}
