/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexQualifiedName;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSignature;
import net.sourceforge.pmd.lang.ast.QualifiedName;
import net.sourceforge.pmd.lang.metrics.MetricMemoizer;
import net.sourceforge.pmd.lang.metrics.ProjectMirror;

/**
 * @author Clément Fournier
 */
public class ApexProjectMirror implements ProjectMirror<ASTUserClass, ASTMethod> {

    private final Map<ApexOperationSignature, Map<ApexQualifiedName, ApexOperationStats>> operations = new HashMap<>();
    private final Map<ApexQualifiedName, ApexClassStats> classes = new HashMap<>();


    void addOperation(ApexQualifiedName qname, ApexOperationSignature sig) {
        if (!operations.containsKey(sig)) {
            operations.put(sig, new HashMap<>());
        }

        operations.get(sig).put(qname, new ApexOperationStats());
    }


    void addClass(ApexQualifiedName qname) {
        classes.put(qname, new ApexClassStats());
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
