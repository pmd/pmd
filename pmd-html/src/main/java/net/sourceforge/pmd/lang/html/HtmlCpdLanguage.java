/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html;

import net.sourceforge.pmd.cpd.AbstractLanguage;
import net.sourceforge.pmd.lang.html.ast.HtmlTokenizer;

public final class HtmlCpdLanguage extends AbstractLanguage {

    public HtmlCpdLanguage() {
        super(HtmlLanguageModule.NAME, HtmlLanguageModule.TERSE_NAME, new HtmlTokenizer(), HtmlLanguageModule.EXTENSIONS);
    }
}
