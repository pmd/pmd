/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.oom.api.ClassMetricKey;
import net.sourceforge.pmd.lang.java.oom.api.MetricKey;
import net.sourceforge.pmd.lang.java.oom.api.MetricVersion;
import net.sourceforge.pmd.lang.java.oom.api.OperationMetricKey;

/**
 * @author Clément Fournier
 */
public class ParameterizedMetricKeyTest {

    @Test
    public void testIdentity() {
        for (ClassMetricKey key : ClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            assertEquals(key1, key2);
            assertTrue(key1 == key2);
        }

        for (OperationMetricKey key : OperationMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            assertEquals(key1, key2);
            assertTrue(key1 == key2);
        }
    }


    @Test
    public void testVersioning() {
        for (ClassMetricKey key : ClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, Version.DUMMY2);
            assertNotEquals(key1, key2);
            assertFalse(key1 == key2);
        }

        for (OperationMetricKey key : OperationMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            ParameterizedMetricKey key2 = ParameterizedMetricKey.getInstance(key, Version.DUMMY2);
            assertNotEquals(key1, key2);
            assertFalse(key1 == key2);
        }
    }


    @Test
    public void testToString() {
        for (ClassMetricKey key : ClassMetricKey.values()) {
            ParameterizedMetricKey key1 = ParameterizedMetricKey.getInstance(key, Version.DUMMY1);
            assertTrue(key1.toString().contains(key1.key.name()));
            assertTrue(key1.toString().contains(key1.version.name()));
        }
    }


    @Test
    public void testAdHocMetricKey() {

        MetricKey<ASTAnyTypeDeclaration> adHocKey = ClassMetricKey.of(null, "metric");

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
