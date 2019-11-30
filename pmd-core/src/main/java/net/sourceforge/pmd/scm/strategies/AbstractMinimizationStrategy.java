/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.strategies;

import net.sourceforge.pmd.lang.ast.Node;

public abstract class AbstractMinimizationStrategy implements MinimizationStrategy {
    public abstract static class AbstractConfiguration implements MinimizationStrategyConfiguration {
    }

    public abstract static class AbstractFactory implements MinimizationStrategyConfigurationFactory {
        private final String name;

        AbstractFactory(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    protected AbstractMinimizationStrategy(AbstractConfiguration configuration) { // NOPMD
        // do nothing for now
    }

    @Override
    public void initialize(Node rootNode) {
        // do nothing by default
    }
}
