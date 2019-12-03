/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.scm.invariants.DummyInvariant;
import net.sourceforge.pmd.scm.invariants.ExitCodeInvariant;
import net.sourceforge.pmd.scm.invariants.InvariantConfiguration;
import net.sourceforge.pmd.scm.invariants.InvariantConfigurationFactory;
import net.sourceforge.pmd.scm.invariants.PrintedMessageInvariant;
import net.sourceforge.pmd.scm.strategies.GreedyStrategy;
import net.sourceforge.pmd.scm.strategies.MinimizationStrategyConfiguration;
import net.sourceforge.pmd.scm.strategies.MinimizationStrategyConfigurationFactory;
import net.sourceforge.pmd.scm.strategies.XPathStrategy;

public abstract class BaseMinimizerLanguageModule implements Language, NodeInformationProvider {
    private final net.sourceforge.pmd.lang.Language pmdLanguage;
    private final Map<String, MinimizationStrategyConfigurationFactory> strategies = new LinkedHashMap<>();
    private final Map<String, InvariantConfigurationFactory> invariantCheckers = new LinkedHashMap<>();

    BaseMinimizerLanguageModule(net.sourceforge.pmd.lang.Language pmdLanguage) {
        this.pmdLanguage = pmdLanguage;
        addInvariant(DummyInvariant.FACTORY);
        addInvariant(ExitCodeInvariant.FACTORY);
        addInvariant(PrintedMessageInvariant.FACTORY);
        addStrategy(XPathStrategy.FACTORY);
        addStrategy(GreedyStrategy.FACTORY);
    }

    protected void addStrategy(MinimizationStrategyConfigurationFactory factory) {
        strategies.put(factory.getName(), factory);
    }

    protected void addInvariant(InvariantConfigurationFactory factory) {
        invariantCheckers.put(factory.getName(), factory);
    }

    @Override
    public String getTerseName() {
        return pmdLanguage.getTerseName();
    }

    @Override
    public List<String> getStrategyNames() {
        return new ArrayList<>(strategies.keySet());
    }

    @Override
    public MinimizationStrategyConfiguration createStrategyConfiguration(String name) {
        MinimizationStrategyConfigurationFactory factory = strategies.get(name);
        return factory == null ? null : factory.createConfiguration();
    }

    @Override
    public List<String> getInvariantNames() {
        return new ArrayList<>(invariantCheckers.keySet());
    }

    @Override
    public InvariantConfiguration createInvariantConfiguration(String name) {
        InvariantConfigurationFactory factory = invariantCheckers.get(name);
        return factory == null ? null : factory.createConfiguration();
    }

    @Override
    public List<String> getLanguageVersions() {
        List<String> result = new ArrayList<>();
        for (LanguageVersion version : pmdLanguage.getVersions()) {
            result.add(version.getVersion());
        }
        return result;
    }

    @Override
    public String getDefaultLanguageVersion() {
        return pmdLanguage.getDefaultVersion().getVersion();
    }

    @Override
    public NodeInformationProvider getNodeInformationProvider() {
        return this;
    }

    @Override
    public Parser getParser(String languageVersion) {
        for (LanguageVersion version : pmdLanguage.getVersions()) {
            if (version.getVersion().equals(languageVersion)) {
                ParserOptions opts = version.getLanguageVersionHandler().getDefaultParserOptions();
                return version.getLanguageVersionHandler().getParser(opts);
            }
        }
        return null;
    }

    public Parser getDefaultParser() {
        return getParser(getDefaultLanguageVersion());
    }

    @Override
    public Set<Node> getDirectDependencies(Node node) {
        // no need to calculate dependencies since there are no dependencies implemented at all, by default
        return Collections.EMPTY_SET;
    }

    @Override
    public Set<Node> getDirectlyDependingNodes(Node node) {
        // no need to calculate dependencies since there are no dependencies implemented at all, by default
        return Collections.EMPTY_SET;
    }
}
