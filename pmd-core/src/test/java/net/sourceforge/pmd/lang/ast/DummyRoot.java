/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Map;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextPos2d;

public class DummyRoot extends DummyNode implements GenericNode<DummyNode>, RootNode {

    private Map<Integer, String> suppressMap = Collections.emptyMap();
    private String filename = "sample.dummy";
    private LanguageVersion languageVersion = DummyLanguageModule.INSTANCE.getDefaultVersion();
    private String sourceText = "dummy text";


    public DummyRoot withLanguage(LanguageVersion languageVersion) {
        this.languageVersion = languageVersion;
        return this;
    }

    public DummyRoot withSourceText(String sourceText) {
        this.sourceText = sourceText;
        return this;
    }


    public DummyRoot fakeTextWithNLines(int numLines, int lineWidth) {
        StringBuilder sb = new StringBuilder(numLines * lineWidth);
        for (int i = 0; i < numLines; i++) {
            for (int j = 0; j < lineWidth; j++) {
                sb.append('@');
            }
            sb.append('\n');
        }
        this.sourceText = sb.toString();
        return this;
    }

    public DummyRoot withNoPmdComments(Map<Integer, String> suppressMap) {
        this.suppressMap = suppressMap;
        return this;
    }

    @Override
    public DummyNode setCoords(int bline, int bcol, int eline, int ecol) {
        @SuppressWarnings("PMD.CloseResource")
        TextDocument doc = getAstInfo().getTextDocument();
        checkInRange(bline, bcol, doc);
        checkInRange(eline, ecol, doc);
        return super.setCoords(bline, bcol, eline, ecol);
    }

    public DummyNode setCoordsReplaceText(int bline, int bcol, int eline, int ecol) {
        fakeTextWithNLines(eline, Math.max(bcol, ecol));
        return setCoords(bline, bcol, eline, ecol);
    }

    private void checkInRange(int line, int col, TextDocument doc) {
        TextPos2d start = TextPos2d.pos2d(line, col);
        assert doc.isInRange(start) : "position out of range " + start + ", text:\n" + sourceText;
    }

    public DummyRoot withFileName(String filename) {
        this.filename = filename;
        return this;
    }


    @Override
    public AstInfo<DummyRoot> getAstInfo() {
        return new AstInfo<>(
            TextDocument.readOnlyString(sourceText, filename, languageVersion),
            this,
            suppressMap
        );
    }

    @Override
    public String getXPathNodeName() {
        return "dummyRootNode";
    }

}
