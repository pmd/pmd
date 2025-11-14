/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.metrics.LanguageMetricsProvider;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParser;
import net.sourceforge.pmd.lang.plsql.metrics.PlsqlMetrics;

/**
 * Implementation of LanguageVersionHandler for the PLSQL AST. It uses anonymous
 * classes as adapters of the visitors to the VisitorStarter interface.
 *
 * @author sturton - PLDoc - pldoc.sourceforge.net
 */
public class PLSQLHandler extends AbstractPmdLanguageVersionHandler {
    private final PlsqlMetricsProvider metricsProvider = new PlsqlMetricsProvider();

    @Override
    public Parser getParser() {
        return new PLSQLParser();
    }

    @Override
    public LanguageMetricsProvider getLanguageMetricsProvider() {
        return metricsProvider;
    }

    private static final class PlsqlMetricsProvider implements LanguageMetricsProvider {
        @Override
        public Set<Metric<?, ?>> getMetrics() {
            return setOf(PlsqlMetrics.NCSS);
        }
    }
}
