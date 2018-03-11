/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.multifile;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ast.ApexQualifiedName;
import net.sourceforge.pmd.lang.apex.metrics.ApexSignatureMatcher;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSigMask;

/**
 * Equivalent to PackageStats in the java framework.
 *
 * @author Clément Fournier
 */
final class ApexProjectMirror implements ApexSignatureMatcher {

    static final ApexProjectMirror INSTANCE = new ApexProjectMirror();

    private final Map<ApexQualifiedName, ApexClassStats> classes = new HashMap<>();

    private ApexProjectMirror() {
    }


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
