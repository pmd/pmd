/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Abstract test rule for a metric. Tests of metrics use the standard
 * framework for rule testing, using one dummy rule per metric. Default
 * parameters can be overridden by overriding the protected methods of
 * this class.
 *
 * @param <N> Result type of the metric. The nested subclasses provide
 *            defaults for common result types
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetricTestRule<N extends Number & Comparable<N>> extends AbstractRule {

    private final PropertyDescriptor<List<MetricOption>> optionsDescriptor =
        PropertyFactory.enumListProperty("metricOptions", optionMappings())
                       .desc("Choose a variant of the metric or the standard")
                       .emptyDefaultValue()
                       .build();

    private final PropertyDescriptor<String> reportLevelDescriptor =
        PropertyFactory.stringProperty("reportLevel")
                       .desc("Minimum value required to report")
                       .defaultValue("" + defaultReportLevel())
                       .build();

    private final Metric<?, N> metric;

    public AbstractMetricTestRule(Metric<?, N> metric) {
        this.metric = metric;

        definePropertyDescriptor(reportLevelDescriptor);
        definePropertyDescriptor(optionsDescriptor);
    }

    protected abstract N parseReportLevel(String value);

    protected boolean reportOn(Node node) {
        return metric.supports(node);
    }

    /**
     * Mappings of labels to options for use in the options property.
     *
     * @return A map of labels to options
     */
    protected Map<String, MetricOption> optionMappings() {
        return new HashMap<>();
    }


    /**
     * Default report level, which is 0.
     *
     * @return The default report level.
     */
    protected abstract N defaultReportLevel();

    protected String violationMessage(Node node, N result) {
        return MessageFormat.format("At line {0} level {1}", node.getBeginLine(), result);
    }

    @Override
    public void apply(Node target, RuleContext ctx) {
        if (reportOn(target)) {
            MetricOptions options = MetricOptions.ofOptions(getProperty(optionsDescriptor));
            N reportLevel = parseReportLevel(getProperty(reportLevelDescriptor));
            N result = Metric.compute(metric, target, options);

            if (result != null && reportLevel.compareTo(result) <= 0) {
                addViolationWithMessage(ctx, target, violationMessage(target, result));
            }
        }

        // visit the whole tree
        for (Node child : target.children()) {
            apply(child, ctx);
        }
    }


    public abstract static class OfInt extends AbstractMetricTestRule<Integer> {

        protected OfInt(Metric<?, Integer> metric) {
            super(metric);
        }

        @Override
        protected Integer parseReportLevel(String value) {
            return Integer.parseInt(value);
        }

        @Override
        protected Integer defaultReportLevel() {
            return 0;
        }
    }

    public abstract static class OfDouble extends AbstractMetricTestRule<Double> {

        protected OfDouble(Metric<?, Double> metric) {
            super(metric);
        }

        @Override
        protected Double parseReportLevel(String value) {
            return Double.parseDouble(value);
        }

        @Override
        protected Double defaultReportLevel() {
            return 0.;
        }
    }

}
