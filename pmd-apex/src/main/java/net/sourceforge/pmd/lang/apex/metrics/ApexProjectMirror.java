/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexQualifiedName;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSigMask;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSignature;
import net.sourceforge.pmd.lang.ast.QualifiedName;
import net.sourceforge.pmd.lang.metrics.MetricMemoizer;
import net.sourceforge.pmd.lang.metrics.ProjectMirror;

/**
 * @author Clément Fournier
 */
public class ApexProjectMirror implements ProjectMirror<ASTUserClass, ASTMethod>, ApexSignatureMatcher {

    private final Map<ApexOperationSignature, Map<ApexQualifiedName, ApexOperationStats>> operations = new HashMap<>();
    private final Map<ApexQualifiedName, ApexClassStats> classes = new HashMap<>();


    void reset() {
        operations.clear();
        classes.clear();
    }


    ApexOperationStats addOperation(ApexQualifiedName qname, ApexOperationSignature sig) {
        if (!operations.containsKey(sig)) {
            operations.put(sig, new HashMap<>());
        }

        ApexOperationStats stats = new ApexOperationStats();
        operations.get(sig).put(qname, stats);
        return stats;
    }


    ApexClassStats addClass(ApexQualifiedName qname) {
        ApexClassStats stats = new ApexClassStats();
        classes.put(qname, stats);
        return stats;
    }


    @Override
    public boolean hasMatchingSig(ApexQualifiedName qname, ApexOperationSigMask mask) {
        for (ApexOperationSignature sig : operations.keySet()) {
            if (mask.covers(sig)) {
                if (operations.get(sig).containsKey(qname)) {
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public MetricMemoizer<ASTMethod> getOperationStats(QualifiedName qname) {
        for (Map<ApexQualifiedName, ApexOperationStats> map : operations.values()) {
            ApexOperationStats stats = map.get(qname);
            if (stats != null) {
                return stats;
            }
        }
        return null;
    }


    @Override
    public MetricMemoizer<ASTUserClass> getClassStats(QualifiedName qname) {
        return classes.get(qname);
    }

}
