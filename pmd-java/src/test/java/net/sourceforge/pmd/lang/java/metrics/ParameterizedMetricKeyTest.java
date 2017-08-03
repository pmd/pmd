/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.MetricKeyUtil;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.metrics.api.JavaClassMetricKey;
import net.sourceforge.pmd.lang.java.metrics.api.JavaOperationMetricKey;
import net.sourceforge.pmd.lang.metrics.MetricKey;
import net.sourceforge.pmd.lang.metrics.MetricVersion;
import net.sourceforge.pmd.lang.metrics.ParameterizedMetricKey;

/**
 * @author Clément Fournier
 */
public class ParameterizedMetricKeyTest {

    @Test
    public void testIdentity() {
        for (JavaClassMetricKey key : JavaClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            assertEquals(key1, key2);
            assertTrue(key1 == key2);
        }

        for (JavaOperationMetricKey key : JavaOperationMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            assertEquals(key1, key2);
            assertTrue(key1 == key2);
        }
    }


    @Test
    public void testVersioning() {
        for (JavaClassMetricKey key : JavaClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, Version.DUMMY2);
            assertNotEquals(key1, key2);
            assertFalse(key1 == key2);
        }

        for (JavaOperationMetricKey key : JavaOperationMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, Version.DUMMY2);
            assertNotEquals(key1, key2);
            assertFalse(key1 == key2);
        }
    }


    @Test
    public void testToString() {
        for (JavaClassMetricKey key : JavaClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            assertTrue(key1.toString().contains(key1.key.name()));
            assertTrue(key1.toString().contains(key1.version.name()));
        }
    }


    @Test
    public void testAdHocMetricKey() {

        MetricKey<ASTAnyTypeDeclaration> adHocKey = MetricKeyUtil.of(null, "metric");

        MetricVersion adHocVersion = new MetricVersion() {
            @Override
            public String name() {
                return "version";
            }
        };

        ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(adHocKey, adHocVersion);
        ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(adHocKey, adHocVersion);

        assertNotNull(key1);
        assertNotNull(key2);
        assertTrue(key1 == key2);
        assertEquals(key1, key2);
        assertTrue(key1.toString().contains(key1.key.name()));
        assertTrue(key1.toString().contains(key1.version.name()));

    }


    private enum Version implements MetricVersion {
        DUMMY1,
        DUMMY2
    }


}
