/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;

/**
 * @author Donald A. Leckie,
 * @version $Revision: 5956 $, $Date: 2008-04-04 04:59:25 -0500 (Fri, 04 Apr 2008) $
 * @since January 14, 2003
 */
public class CyclomaticComplexityRule extends StdCyclomaticComplexityRule {

    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        if (!isSuppressed(node)) {
            ClassEntry classEntry = entryStack.peek();

            int cyclo = (int) Metrics.get(OperationMetricKey.CYCLO, node);
            classEntry.numMethods++;
            classEntry.totalCyclo += cyclo;
            if (cyclo > classEntry.maxCyclo) {
                classEntry.maxCyclo = cyclo;
            }

            if (showMethodsComplexity && cyclo >= reportLevel) {
                addViolation(data, node, new String[]
                    {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                     node.getQualifiedName().getOperation(),
                     String.valueOf(cyclo),});
                System.err.println(new String[] {node instanceof ASTMethodDeclaration ? "method" : "constructor",
                                                 node.getQualifiedName().getOperation(),
                                                 String.valueOf(cyclo),});
            }
        }
        return data;
    }
}
