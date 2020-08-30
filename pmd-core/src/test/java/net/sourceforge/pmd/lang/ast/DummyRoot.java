/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;
import net.sourceforge.pmd.util.document.TextDocument;

public class DummyRoot extends DummyNode implements GenericNode<DummyNode>, RootNode {

    private final Map<Integer, String> suppressMap;
    private LanguageVersion languageVersion;
    private String filename = "sample.dummy";

    public DummyRoot(Map<Integer, String> suppressMap, LanguageVersion languageVersion) {
        super();
        this.suppressMap = suppressMap;
        this.languageVersion = languageVersion;
        setCoords(1, 1, 1, 1);
    }

    public DummyRoot(Map<Integer, String> suppressMap) {
        this(suppressMap, LanguageRegistry.getLanguage(DummyLanguageModule.NAME).getDefaultVersion());
    }

    public DummyRoot() {
        this(Collections.emptyMap());
    }

    public DummyRoot(LanguageVersion languageVersion) {
        this(Collections.emptyMap(), languageVersion);
    }


    public DummyRoot withLanguage(LanguageVersion languageVersion) {
        this.languageVersion = languageVersion;
        return this;
    }

    public DummyRoot withFileName(String filename) {
        this.filename = filename;
        return this;
    }

    @Override
    public @NonNull TextDocument getTextDocument() {
        return TextDocument.readOnlyString("dummy text", filename, languageVersion);
    }

    @Override
    public Map<Integer, String> getNoPmdComments() {
        return suppressMap;
    }

    @Override
    public String getXPathNodeName() {
        return "dummyRootNode";
    }

}
