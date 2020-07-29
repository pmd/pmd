/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.Map;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;

public class DummyRoot extends DummyNode implements GenericNode<DummyNode>, RootNode {

    private final Map<Integer, String> suppressMap;
    private LanguageVersion languageVersion;

    public DummyRoot(Map<Integer, String> suppressMap, LanguageVersion languageVersion) {
        super();
        this.suppressMap = suppressMap;
        this.languageVersion = languageVersion;
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


    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    public DummyRoot setLanguageVersion(LanguageVersion languageVersion) {
        this.languageVersion = languageVersion;
        return this;
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
