/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ast.ApexQualifiedName;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSigMask;

/**
 * @author Clément Fournier
 */
public class ApexProjectMirror implements ApexSignatureMatcher {

    private final Map<ApexQualifiedName, ApexClassStats> classes = new HashMap<>();


    void reset() {
        classes.clear();
    }


    ApexClassStats getClassStats(ApexQualifiedName qname, boolean createIfNotFound) {
        ApexQualifiedName className = qname.getClassName();
        if (createIfNotFound && !classes.containsKey(className)) {
            classes.put(className, new ApexClassStats());
        }
        return classes.get(className);
    }


    @Override
    public boolean hasMatchingSig(ApexQualifiedName qname, ApexOperationSigMask mask) {
        ApexClassStats classStats = getClassStats(qname, false);

        return classStats != null && classStats.hasMatchingSig(qname.getOperation(), mask);

    }




}
