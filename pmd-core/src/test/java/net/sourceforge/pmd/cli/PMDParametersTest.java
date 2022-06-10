/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PMDParametersTest {

    @Test
    void testVersion() throws Exception {
        PMDParameters parameters = new PMDParameters();
        // no language set, uses default language
        Assertions.assertEquals("1.7", parameters.getVersion());

        // now set language
        FieldUtils.writeDeclaredField(parameters, "language", "dummy2", true);
        Assertions.assertEquals("1.0", parameters.getVersion());
    }
}
