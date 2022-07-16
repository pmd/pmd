/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.html.HtmlLanguageModule;

public final class HtmlParsingHelper extends BaseParsingHelper<HtmlParsingHelper, ASTHtmlDocument> {

    public static final HtmlParsingHelper DEFAULT = new HtmlParsingHelper(Params.getDefault());

    private HtmlParsingHelper(Params params) {
        super(HtmlLanguageModule.NAME, ASTHtmlDocument.class, params);
    }

    @Override
    protected HtmlParsingHelper clone(Params params) {
        return new HtmlParsingHelper(params);
    }
}
