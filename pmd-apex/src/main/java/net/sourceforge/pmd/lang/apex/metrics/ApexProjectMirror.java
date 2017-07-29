/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexQualifiedName;
import net.sourceforge.pmd.lang.ast.QualifiedName;
import net.sourceforge.pmd.lang.metrics.MetricMemoizer;
import net.sourceforge.pmd.lang.metrics.ProjectMirror;

/**
 * @author Clément Fournier
 */
public class ApexProjectMirror implements ProjectMirror<ASTUserClass, ASTMethod> {

    private final Map<ApexQualifiedName, ApexOperationStats> operations = new HashMap<>();
    private final Map<ApexQualifiedName, ApexClassStats> classes = new HashMap<>();

    @Override
    public MetricMemoizer<ASTMethod> getOperationStats(QualifiedName qname) {
        return null;
    }


    @Override
    public MetricMemoizer<ASTUserClass> getClassStats(QualifiedName qname) {
        return null;
    }

}
