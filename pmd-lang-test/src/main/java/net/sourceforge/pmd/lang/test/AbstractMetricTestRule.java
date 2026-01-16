/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.test;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.metrics.Metric;
import net.sourceforge.pmd.lang.metrics.MetricOption;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;


/**
 * Abstract test rule for a metric. Tests of metrics use the standard
 * framework for rule testing, using one dummy rule per metric. Default
 * parameters can be overridden by overriding the protected methods of
 * this class.
 *
 * @param <N> Result type of the metric. The nested subclasses provide
 *            defaults for common result types
 * @param <O> The enum type of the {@link MetricOption}. If the metric doesn't
 *            support options, then {@link NoOptions} can be used as
 *            a placeholder.
 *
 * @author Clément Fournier
 */
public abstract class AbstractMetricTestRule<N extends Number & Comparable<N>, O extends Enum<O> & MetricOption> extends AbstractRule {

    public enum NoOptions implements MetricOption {
        VOID
    }

    private final PropertyDescriptor<List<O>> optionsDescriptor;

    private final PropertyDescriptor<String> reportLevelDescriptor =
        PropertyFactory.stringProperty("reportLevel")
                       .desc("Minimum value required to report")
                       .defaultValue("" + defaultReportLevel())
                       .build();

    private final Metric<?, N> metric;

    public AbstractMetricTestRule(Metric<?, N> metric) {
        this(metric, null);
    }

    public AbstractMetricTestRule(Metric<?, N> metric, Class<O> metricOptionsEnum) {
        this.metric = metric;
        if (metricOptionsEnum != null) {
            this.optionsDescriptor =
                    PropertyFactory.conventionalEnumListProperty("metricOptions", metricOptionsEnum)
                            .desc("Choose a variant of the metric or the standard")
                            .emptyDefaultValue()
                            .build();
            definePropertyDescriptor(optionsDescriptor);
        } else {
            this.optionsDescriptor = null;
        }

        definePropertyDescriptor(reportLevelDescriptor);
    }

    protected abstract N parseReportLevel(String value);

    protected boolean reportOn(Node node) {
        return metric.supports(node);
    }

    /**
     * Mappings of labels to options for use in the options property.
     *
     * @return A map of labels to options
     * @deprecated Since 7.21.0. No extra mapping is required anymore. The {@link MetricOption} enum
     * values are used. See {@link #AbstractMetricTestRule(Metric, Class)} to provide the
     * enum at construction time.
     */
    @Deprecated
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
            MetricOptions options = MetricOptions.emptyOptions();
            if (optionsDescriptor != null) {
                options = MetricOptions.ofOptions(getProperty(optionsDescriptor));
            }
            N reportLevel = parseReportLevel(getProperty(reportLevelDescriptor));
            N result = Metric.compute(metric, target, options);

            if (result != null && reportLevel.compareTo(result) <= 0) {
                ctx.addViolationWithMessage(target, violationMessage(target, result));
            }
        }

        // visit the whole tree
        for (Node child : target.children()) {
            apply(child, ctx);
        }
    }


    public abstract static class OfInt extends OfIntWithOptions<NoOptions> {
        protected OfInt(Metric<?, Integer> metric) {
            super(metric, null);
        }
    }

    public abstract static class OfIntWithOptions<O extends Enum<O> & MetricOption> extends AbstractMetricTestRule<Integer, O> {
        protected OfIntWithOptions(Metric<?, Integer> metric, Class<O> metricOptionsEnum) {
            super(metric, metricOptionsEnum);
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

    public abstract static class OfDouble extends AbstractMetricTestRule<Double, NoOptions> {

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
