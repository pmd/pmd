/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;

public class ParameterizedMetricKeyTest {

    private static final MetricOptions DUMMY_VERSION_1 = MetricOptions.ofOptions(Options.DUMMY1, Options.DUMMY2);
    private static final MetricOptions DUMMY_VERSION_2 = MetricOptions.ofOptions(Options.DUMMY2);
    private static final Metric<Node, Double> DUMMY_METRIC = Metric.of((n, opts) -> 0., t -> t, "dummy");

    @Test
    public void testIdentity() {

        ParameterizedMetricKey<Node, ?> key1 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_1);
        ParameterizedMetricKey<Node, ?> key2 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_1);
        assertEquals(key1, key2);
        assertSame(key1, key2);
    }


    @Test
    public void testVersioning() {

        ParameterizedMetricKey<Node, ?> key1 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_1);
        ParameterizedMetricKey<Node, ?> key2 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_2);
        assertNotEquals(key1, key2);
        assertNotSame(key1, key2);
    }


    @Test
    public void testToString() {

        ParameterizedMetricKey<Node, ?> key1 = ParameterizedMetricKey.getInstance(DUMMY_METRIC, DUMMY_VERSION_1);
        assertTrue(key1.toString().contains(key1.metric.displayName()));
        assertTrue(key1.toString().contains(key1.options.toString()));
    }


    @Test
    public void testAdHocMetricKey() {

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
