/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

/**
 * @author Clément Fournier
 * @since 6.2.0
 */
public class NestedAllocationExpressions {
    static {
        new Thread(new Runnable() {
            // missing
            public void run() {

            }
        }).start();
    }
}
