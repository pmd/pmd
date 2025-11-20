/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import net.sourceforge.pmd.util.StringUtil;

/**
 * Option to pass to a metric. Options modify the behaviour of a metric.
 * You must bundle them into a {@link MetricOptions} to pass them all to a metric.
 *
 * <p>Options must be suitable for use in sets (implement equals/hashcode,
 * or be singletons).
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public interface MetricOption {

    /**
     * Returns the name of the option constant.
     *
     * @return The name of the option constant.
     */
    String name();

    /**
     * Returns the name of the option as it should be used in properties.
     *
     * @return The name of the option.
     * @deprecated Since 7.19.0. When metrics are used for (rule) properties, then the default
     * enum mapping (from SCREAMING_SNAKE_CASE to camelCase) will be used for the enum values.
     * See {@link net.sourceforge.pmd.properties.PropertyFactory#enumListPropertyNew(String, Class)}.
     */
    @Deprecated
    default String valueName() {
        return StringUtil.CaseConvention.SCREAMING_SNAKE_CASE.convertTo(StringUtil.CaseConvention.CAMEL_CASE, name());
    }
}
