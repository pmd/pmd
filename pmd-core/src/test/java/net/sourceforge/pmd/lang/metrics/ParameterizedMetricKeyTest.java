/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.Node;

class ParameterizedMetricKeyTest {

    private static final MetricOptions DUMMY_VERSION_1 = MetricOptions.ofOptions(Options.DUMMY1, Options.DUMMY2);
    private static final MetricOptions DUMMY_VERSION_2 = MetricOptions.ofOptions(Options.DUMMY2);
    private static final Metric<Node, Double> DUMMY_METRIC = Metric.of((n, opts) -> 0., t -> t, "dummy");

    @Test
    void testIdentity() {

        ParameterizedMetricKey<Node, ?> key1 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_1);
        ParameterizedMetricKey<Node, ?> key2 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_1);
        assertEquals(key1, key2);
        assertSame(key1, key2);
    }


    @Test
    void testVersioning() {

        ParameterizedMetricKey<Node, ?> key1 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_1);
        ParameterizedMetricKey<Node, ?> key2 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_2);
        assertNotEquals(key1, key2);
        assertNotSame(key1, key2);
    }


    @Test
    void testToString() {

        ParameterizedMetricKey<Node, ?> key1 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_1);
        assertTrue(key1.toString().contains(key1.metric.displayName()));
        assertTrue(key1.toString().contains(key1.options.toString()));
    }


    @Test
    void testAdHocMetricKey() {

        ParameterizedMetricKey<Node, ?> key1 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_1);
        ParameterizedMetricKey<Node, ?> key2 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_1);

        assertNotNull(key1);
        assertNotNull(key2);
        assertSame(key1, key2);
        assertEquals(key1, key2);
        assertTrue(key1.toString().contains(key1.metric.displayName()));
        assertTrue(key1.toString().contains(key1.options.toString()));

    }

    private enum Options implements MetricOption {
        DUMMY1,
        DUMMY2;


        @Override
        public String valueName() {
            return null;
        }
    }


}
