/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.html.ast.HtmlParser;

class HtmlHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public Parser getParser() {
        return new HtmlParser();
    }

}
