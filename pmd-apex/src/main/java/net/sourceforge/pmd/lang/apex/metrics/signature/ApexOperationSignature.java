/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.signature;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.metrics.Signature;

/**
 * @author Clément Fournier
 */
public class ApexOperationSignature implements Signature<ASTMethod> {

    private String foo;


    /**
     * Builds the signature of this node.
     *
     * @param node The method node
     *
     * @return The signature of the node
     */
    public static ApexOperationSignature of(ASTMethod node) {
        return null;
    }

}
