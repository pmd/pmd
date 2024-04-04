/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

import net.sourceforge.pmd.lang.html.HtmlLanguageModule;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

public final class HtmlParsingHelper extends BaseParsingHelper<HtmlParsingHelper, ASTHtmlDocument> {

    public static final HtmlParsingHelper DEFAULT = new HtmlParsingHelper(Params.getDefault());

    private HtmlParsingHelper(Params params) {
        super(HtmlLanguageModule.getInstance(), ASTHtmlDocument.class, params);
    }

    @Override
    protected HtmlParsingHelper clone(Params params) {
        return new HtmlParsingHelper(params);
    }
}
