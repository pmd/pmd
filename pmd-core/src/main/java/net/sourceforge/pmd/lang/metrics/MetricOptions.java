/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Bundles a set of options to pass to a metric. Metrics may use these options as they see fit.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class MetricOptions {

    private static final Map<MetricOptions, MetricOptions> POOL = new HashMap<>();
    private static final MetricOptions EMPTY_OPTIONS;
    private Set<MetricOption> options;


    static {
        EMPTY_OPTIONS = new MetricOptions();
        POOL.put(EMPTY_OPTIONS, EMPTY_OPTIONS);
    }


    private MetricOptions() {
        options = Collections.emptySet();
    }


    private MetricOptions(Set<? extends MetricOption> opts) {

        switch (opts.size()) {
        case 0:
            options = Collections.emptySet();
            break;
        case 1:
            options = Collections.<MetricOption>singleton(opts.iterator().next());
            break;
        default:
            options = Collections.unmodifiableSet(opts);
            break;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MetricOptions other = (MetricOptions) o;

        return options.equals(other.options);
    }


    @Override
    public int hashCode() {
        return options.hashCode();
    }


    /**
     * Returns an immutable set of options. Metrics may use these options as they see fit.
     *
     * @return The set of options of this version
     */
    public Set<MetricOption> getOptions() {
        return options;
    }


    /**
     * Returns true if this bundle contains the given option.
     *
     * @param option Option to look for
     */
    public boolean contains(MetricOption option) {
        return options.contains(option);
    }


    @Override
    public String toString() {
        return "MetricOptions{"
            + "options=" + options
            + '}';
    }


    /**
     * Returns an empty options bundle.
     *
     * @return An empty options bundle
     */

    public static MetricOptions emptyOptions() {
        return EMPTY_OPTIONS;
    }


    /**
     * Gets an options bundle from a collection of options.
     *
     * @param options The options to build the bundle from
     *
     * @return An options bundle
     */
    public static MetricOptions ofOptions(Collection<? extends MetricOption> options) {
        MetricOptionsBuilder builder = new MetricOptionsBuilder();
        builder.addAll(options);
        return builder.build();
    }


    /**
     * Gets an options bundle from options.
     *
     * @param option  Mandatory first argument
     * @param options Rest of the options
     *
     * @return An options bundle
     */
    public static MetricOptions ofOptions(MetricOption option, MetricOption... options) {
        MetricOptionsBuilder builder = new MetricOptionsBuilder();

        builder.add(option);

        for (MetricOption opt : options) {
            builder.add(opt);
        }

        return builder.build();
    }


    private static class MetricOptionsBuilder {


        private Set<MetricOption> opts = new HashSet<>();


        void add(MetricOption option) {
            if (option != null) {
                opts.add(option);
            }
        }


        void addAll(Collection<? extends MetricOption> options) {
            if (options != null) {
                this.opts.addAll(options);
                opts.remove(null);
            }
        }


        MetricOptions build() {
            if (opts.isEmpty()) {
                return emptyOptions();
            }

            MetricOptions result = new MetricOptions(opts);

            if (!POOL.containsKey(result)) {
                POOL.put(result, result);
            }

            return POOL.get(result);
        }

    }
}
