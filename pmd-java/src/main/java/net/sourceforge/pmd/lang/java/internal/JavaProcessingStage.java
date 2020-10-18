/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.ast.AstAnalysisContext;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaParser;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;
import net.sourceforge.pmd.lang.java.symboltable.SymbolFacade;


/**
 * Java processing stages.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@Experimental
public enum JavaProcessingStage implements AstProcessingStage<JavaProcessingStage> {

    /**
     * This acts as a merged stage, non-optional. Ideally this would be encapsulated
     * in the {@link JavaParser}, like the {@link LanguageLevelChecker}.
     */
    JAVA_PROCESSING("Java processing") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            JavaAstProcessor.create(configuration.getTypeResolutionClassLoader(), configuration.getLanguageVersion(), JavaAstProcessor.defaultLogger(), JavaAstProcessor.defaultTypeInfLogger())
                            .process((ASTCompilationUnit) rootNode);
        }
    },

    /**
     * Symbol table analysis.
     */
    SYMBOL_RESOLUTION("Symbol table") {
        @Override
        public void processAST(RootNode rootNode, AstAnalysisContext configuration) {
            // kept for compatibility with existing tests
            new SymbolFacade().initializeWith(configuration.getTypeResolutionClassLoader(), (ASTCompilationUnit) rootNode);
        }
    };

    private final String displayName;
    private final List<JavaProcessingStage> dependencies;

    JavaProcessingStage(String displayName, JavaProcessingStage... dependencies) {
        this.displayName = displayName;
        this.dependencies = Collections.unmodifiableList(Arrays.asList(dependencies));
    }

    @Override
    public List<JavaProcessingStage> getDependencies() {
        return dependencies;
    }


    @Override
    public String getDisplayName() {
        return displayName;
    }


    @Override
    public final Language getLanguage() {
        return LanguageRegistry.findLanguageByTerseName("java");
    }
}
