/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class DesignerUtilTest {

    @Test
    public void testGetFxml() {
        assertNotNull(DesignerUtil.getFxml("designer.fxml"));
        assertNotNull(DesignerUtil.getFxml("xpath.fxml"));
    }


}
