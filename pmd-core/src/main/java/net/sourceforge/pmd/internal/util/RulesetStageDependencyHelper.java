/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.SourceCodeProcessor;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.AstAnalysisContext;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.RootNode;

/**
 * Temporary helper for {@link SourceCodeProcessor} until we change rulesets.
 * See comment in SourceCodeProcessor.
 *
 * @author Clément Fournier
 */
public class RulesetStageDependencyHelper {

    // cache by ruleset
    private final Map<RuleSets, Map<LanguageVersion, List<AstProcessingStage<?>>>> dependenciesByRuleset = new HashMap<>();
    private final PMDConfiguration configuration;

    public RulesetStageDependencyHelper(PMDConfiguration configuration) {
        this.configuration = configuration;
    }

    /** Gets the stage dependencies of the ruleset for the given language version. */
    private List<AstProcessingStage<?>> getDependencies(RuleSets ruleSets, LanguageVersion languageVersion) {
        Map<LanguageVersion, List<AstProcessingStage<?>>> byLanguage = dependenciesByRuleset.computeIfAbsent(ruleSets, r -> new HashMap<>());

        return byLanguage.computeIfAbsent(languageVersion, l -> buildDependencyList(ruleSets, l));
    }


    public void runLanguageSpecificStages(RuleSets ruleSets, LanguageVersion languageVersion, RootNode rootNode) {
        AstAnalysisContext context = buildContext(languageVersion);

        getDependencies(ruleSets, languageVersion)
            .forEach(stage -> executeProcessingStage(stage, rootNode, context));

    }

    private void executeProcessingStage(AstProcessingStage<?> stage, RootNode root, AstAnalysisContext context) {

        String label = stage.getLanguage().getShortName() + ": " + stage.getDisplayName();
        try (TimedOperation to = TimeTracker.startOperation(TimedOperationCategory.LANGUAGE_SPECIFIC_PROCESSING, label)) {
            stage.processAST(root, context);
        }
    }

    private List<AstProcessingStage<?>> buildDependencyList(RuleSets ruleSets, LanguageVersion languageVersion) {
        List<AstProcessingStage<?>> stages = new ArrayList<>(languageVersion.getLanguageVersionHandler().getProcessingStages());
        List<AstProcessingStage<?>> result = new ArrayList<>();

        for (Rule rule : ruleSets.getAllRules()) {
            if (stages.isEmpty()) {
                return result;
            }
            for (AstProcessingStage<?> stage : stages) {
                if (rule.dependsOn(stage)) {
                    result.add(stage);
                }
            }
            stages.removeAll(result);
        }

        return result.stream()
                     .flatMap(it -> Stream.concat(Stream.of(it), it.getDependencies().stream()))
                     .sorted(AstProcessingStage::compare)
                     .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }


    // TODO this could be made language specific as well
    private AstAnalysisContext buildContext(LanguageVersion languageVersion) {
        return new AstAnalysisContext() {
            @Override
            public ClassLoader getTypeResolutionClassLoader() {
                return configuration.getClassLoader();
            }


            @Override
            public LanguageVersion getLanguageVersion() {
                return languageVersion;
            }
        };
    }

}
