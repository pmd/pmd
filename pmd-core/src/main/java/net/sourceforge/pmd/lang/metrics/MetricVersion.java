/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Gathers a set of options to pass to a metric. Metrics may use these options as they see fit. That's used internally
 * to pass all options at once to a metric.
 *
 * @author Clément Fournier
 */
public class MetricVersion {

    private static final Map<MetricVersion, MetricVersion> POOL = new HashMap<>();
    private Set<MetricOption> options;


    static {
        MetricVersion emptyVersion = new MetricVersion();
        POOL.put(emptyVersion, emptyVersion);
    }


    private MetricVersion(Set<MetricOption> opts) {

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

        MetricVersion version = (MetricVersion) o;

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
        return "MetricVersion{" +
            "options=" + options +
            '}';
    }


    /**
     * Gets a version from a list of options.
     *
     * @param opts The options to build the version from
     *
     * @return A metric version
     */
    public static MetricVersion ofOptions(MetricOption... opts) {

    }

    public static MetricVersion ofOptions(Collection<MetricOption> opts) {
        MetricVersion version = new MetricVersion(new HashSet<>(opts));
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
    public static MetricOption[] toOptions(MetricVersion version) {
        Set<MetricOption> options = version.getOptions();
        return options.toArray(new MetricOption[options.size()]);
    }


}
