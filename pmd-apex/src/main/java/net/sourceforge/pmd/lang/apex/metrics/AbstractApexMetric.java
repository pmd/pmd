/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

/**
 * @author Clément Fournier
 */
public class AbstractApexMetric {

    protected ApexSignatureMatcher getSignatureMatcher() {
        return ApexMetrics.getFacade().getProjectMirror();
    }

}
