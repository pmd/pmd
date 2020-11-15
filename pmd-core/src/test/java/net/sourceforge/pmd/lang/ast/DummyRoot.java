/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Map;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;

public class DummyRoot extends DummyNode implements GenericNode<DummyNode>, RootNode {

    private Map<Integer, String> suppressMap;
    private String filename = "sample.dummy";
    private LanguageVersion languageVersion;
    private String sourceText;


    public DummyRoot withLanguage(LanguageVersion languageVersion) {
        this.languageVersion = languageVersion;
        return this;
    }

    public DummyRoot withSourceText(String sourceText) {
        this.sourceText = sourceText;
        return this;
    }

    public DummyRoot withNoPmdComments(Map<Integer, String> suppressMap) {
        this.suppressMap = suppressMap;
        return this;
    }


    public DummyRoot withFileName(String filename) {
        this.filename = filename;
        return this;
    }


    @Override
    public AstInfo<DummyRoot> getAstInfo() {
        return new AstInfo<>(
            filename,
            languageVersion,
            sourceText,
            this,
            suppressMap
        );
    }

    @Override
    public String getXPathNodeName() {
        return "dummyRootNode";
    }

}
