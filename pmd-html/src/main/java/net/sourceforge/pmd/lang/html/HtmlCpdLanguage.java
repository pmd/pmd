/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html;

import net.sourceforge.pmd.cpd.AbstractLanguage;

public final class HtmlCpdLanguage extends AbstractLanguage {

    public HtmlCpdLanguage() {
        super("HTML", "html", new HtmlTokenizer(), ".html");
    }
}
