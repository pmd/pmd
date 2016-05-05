/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Assert;
import org.junit.Test;

public class PMDParametersTest {

    @Test
    public void testVersion() throws Exception {
        PMDParameters parameters = new PMDParameters();
        // no language set, uses default language
        Assert.assertEquals("1.7", parameters.getVersion());

        // now set lanuage
        FieldUtils.writeDeclaredField(parameters, "language", "dummy2", true);
        Assert.assertEquals("1.0", parameters.getVersion());
    }
}
