/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.oom.Metrics;
import net.sourceforge.pmd.lang.java.oom.Metrics.OperationMetricKey;

/**
 * Implements the modified cyclomatic complexity rule
 * <p>
 * Modified rules: Same as standard cyclomatic complexity, but switch statement
 * plus all cases count as 1.
 *
 * @author Alan Hohn, based on work by Donald A. Leckie
 * @since June 18, 2014
 */
public class ModifiedCyclomaticComplexityRule extends StdCyclomaticComplexityRule {

    public ModifiedCyclomaticComplexityRule() {
        super();
    }

    @Override
    public Object visit(ASTMethodOrConstructorDeclaration node, Object data) {
        if (!isSuppressed(node)) {
            ClassEntry classEntry = entryStack.peek();

            int cyclo = (int) Metrics.get(OperationMetricKey.ModifiedCYCLO, node);
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
