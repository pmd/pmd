/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public abstract class AbstractMetric {
    
    protected static OperationSigMask operationMask;

    protected List<String> findAllCalls(ASTMethodDeclaration node) {
        List<String> result = new ArrayList<>();
        // Find the qualified names of all methods called in that method's block
        return result;
    }
    
    protected boolean checkMaskIsMatching(ASTMethodDeclaration node) {
        OperationSignature sig = OperationSignature.buildFor(node);
        return operationMask.covers(sig);
    }

}
