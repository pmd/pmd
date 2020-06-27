/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;

/**
 * @author Clément Fournier
 */
public enum DummyAstStages implements AstProcessingStage<DummyAstStages> {
    FOO,
    BAR,
    RUNS_FOO {
        @Override
        public List<DummyAstStages> getDependencies() {
            return Collections.singletonList(FOO);
        }
    };


    @Override
    public Language getLanguage() {
        return LanguageRegistry.STATIC.findLanguageByTerseName("dummy");
    }

    @Override
    public List<DummyAstStages> getDependencies() {
        return Collections.emptyList();
    }

    public boolean hasProcessed(DummyNode node) {
        return node.getUserData().containsKey(name() + "_STAGE");
    }

    @Override
    public String getDisplayName() {
        return name();
    }

    @Override
    public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
        ((DummyNode) rootNode).getUserData().put(name() + "_STAGE", "done");
    }

}
