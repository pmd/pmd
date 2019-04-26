/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.internal.util.RulesetStageDependencyHelper;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.DummyAstStages;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.AbstractRule;

public class StageDependencyTest {

    private DummyNode process(String source, RuleSets ruleSets) {

        RuleContext context = new RuleContext();

        PMDConfiguration configuration = new PMDConfiguration();
        RulesetStageDependencyHelper helper = new RulesetStageDependencyHelper(configuration);

        LanguageVersion version = LanguageRegistry.findLanguageByTerseName("dummy").getVersion("1.0");
        Parser parser = PMD.parserFor(version, configuration);
        context.setLanguageVersion(version);

        RootNode rootNode = (RootNode) parser.parse("dummyfile.dummy", new StringReader(source));

        helper.runLanguageSpecificStages(ruleSets, version, rootNode);

        return (DummyNode) rootNode;
    }


    @Test
    public void testSimpleDependency() throws PMDException {

        DummyNode root = process("foo bar", singleRule(new PredicateTestRule(DummyAstStages.FOO)));

        Assert.assertTrue(DummyAstStages.FOO.hasProcessed(root));
        Assert.assertFalse(DummyAstStages.BAR.hasProcessed(root));
    }

    @Test
    public void testNoDependency() throws PMDException {

        DummyNode root = process("foo bar", singleRule(new PredicateTestRule()));

        Assert.assertFalse(DummyAstStages.FOO.hasProcessed(root));
        Assert.assertFalse(DummyAstStages.BAR.hasProcessed(root));
    }

    @Test
    public void testDependencyUnion() throws PMDException {

        RuleSets ruleSets = singleRule(new PredicateTestRule(DummyAstStages.FOO));
        ruleSets.addRuleSet(new RuleSetFactory().createSingleRuleRuleSet(new PredicateTestRule(DummyAstStages.BAR)));
        DummyNode root = process("foo bar", ruleSets);

        Assert.assertTrue(DummyAstStages.FOO.hasProcessed(root));
        Assert.assertTrue(DummyAstStages.BAR.hasProcessed(root));
    }

    @Test
    public void testTransitiveDependency() throws PMDException {

        DummyNode root = process("foo bar", singleRule(new PredicateTestRule(DummyAstStages.RUNS_FOO)));

        Assert.assertTrue(DummyAstStages.FOO.hasProcessed(root));
        Assert.assertFalse(DummyAstStages.BAR.hasProcessed(root));
        Assert.assertTrue(DummyAstStages.RUNS_FOO.hasProcessed(root));
    }


    private static RuleSets singleRule(Rule r) {
        return new RuleSets(new RuleSetFactory().createSingleRuleRuleSet(r));
    }

    private static class PredicateTestRule extends AbstractRule {

        private final List<DummyAstStages> dependencies;

        PredicateTestRule(DummyAstStages... dependencies) {
            this.dependencies = Arrays.asList(dependencies);
        }

        @Override
        public Language getLanguage() {
            return LanguageRegistry.findLanguageByTerseName("dummy");
        }

        @Override
        public boolean dependsOn(AstProcessingStage<?> stage) {
            return dependencies.contains(stage);
        }

        @Override
        public void apply(List<? extends Node> nodes, RuleContext ctx) {

        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }
    }


}
