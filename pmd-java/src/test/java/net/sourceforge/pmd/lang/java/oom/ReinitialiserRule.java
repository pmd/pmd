/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom;

import org.junit.rules.ExternalResource;

/**
 * @author Clément Fournier
 */
public class ReinitialiserRule extends ExternalResource {

    @Override
    protected void after() {
        Metrics.reset();
    }
}
