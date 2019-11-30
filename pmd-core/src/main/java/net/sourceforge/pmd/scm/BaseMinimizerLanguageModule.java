/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.scm.invariants.DummyInvariant;
import net.sourceforge.pmd.scm.invariants.ExitCodeInvariant;
import net.sourceforge.pmd.scm.invariants.InvariantConfiguration;
import net.sourceforge.pmd.scm.invariants.InvariantConfigurationFactory;
import net.sourceforge.pmd.scm.invariants.PrintedMessageInvariant;
import net.sourceforge.pmd.scm.strategies.MinimizationStrategyConfiguration;
import net.sourceforge.pmd.scm.strategies.MinimizationStrategyConfigurationFactory;
import net.sourceforge.pmd.scm.strategies.XPathStrategy;

public abstract class BaseMinimizerLanguageModule implements Language {
    private final String terseName;
    private final Map<String, MinimizationStrategyConfigurationFactory> strategies = new LinkedHashMap<>();
    private final Map<String, InvariantConfigurationFactory> invariantCheckers = new LinkedHashMap<>();

    BaseMinimizerLanguageModule(String name) {
        terseName = name;
        addInvariant(DummyInvariant.FACTORY);
        addInvariant(ExitCodeInvariant.FACTORY);
        addInvariant(PrintedMessageInvariant.FACTORY);
        addStrategy(XPathStrategy.FACTORY);
    }

    protected void addStrategy(MinimizationStrategyConfigurationFactory factory) {
        strategies.put(factory.getName(), factory);
    }

    protected void addInvariant(InvariantConfigurationFactory factory) {
        invariantCheckers.put(factory.getName(), factory);
    }

    @Override
    public String getTerseName() {
        return terseName;
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
}
