/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.html.ast.ASTHtmlDocument;

public class HtmlParsingHelper extends BaseParsingHelper<HtmlParsingHelper, ASTHtmlDocument> {

    public static final HtmlParsingHelper DEFAULT = new HtmlParsingHelper(Params.getDefault());

    public HtmlParsingHelper(BaseParsingHelper.Params params) {
        super(HtmlLanguageModule.NAME, ASTHtmlDocument.class, params);
    }

    @Override
    protected @NonNull HtmlParsingHelper clone(@NonNull Params params) {
        return new HtmlParsingHelper(params);
    }
}
