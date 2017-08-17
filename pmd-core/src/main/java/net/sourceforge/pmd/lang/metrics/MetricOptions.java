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


    private MetricOptions(Set<MetricOption> opts) {

        switch (opts.size()) {
        case 0:
            options = Collections.emptySet();
            break;
        case 1:
            options = Collections.singleton(opts.iterator().next());
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

        MetricOptions version = (MetricOptions) o;

        return options.equals(version.options);
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


    @Override
    public String toString() {
        return "MetricOptions{" +
            "options=" + options +
            '}';
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
     * Gets an options bundle from a list of options.
     *
     * @param opts The options to build the bundle from
     *
     * @return An options bundle
     */
    public static MetricOptions ofOptions(Collection<MetricOption> opts) {
        MetricOptions version;
        if (opts instanceof Set) {
            version = new MetricOptions((Set<MetricOption>) opts);
        } else {
            version = new MetricOptions(new HashSet<>(opts));
        }

        if (!POOL.containsKey(version)) {
            POOL.put(version, version);
        }

        return POOL.get(version);
    }


    /**
     * Gets an array of options from a version.
     *
     * @param version The version to decompose
     *
     * @return An array of options
     */
    public static MetricOption[] toOptions(MetricOptions version) {
        Set<MetricOption> options = version.getOptions();
        return options.toArray(new MetricOption[options.size()]);
    }


}
