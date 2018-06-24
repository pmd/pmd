/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics.signature;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.metrics.Signature;

/**
 * @author Clément Fournier
 */
public final class ApexOperationSignature extends ApexSignature implements Signature<ASTMethod> {

    private static final Map<Integer, ApexOperationSignature> POOL = new HashMap<>();


    /**
     * Create a signature using its visibility.
     *
     * @param visibility The visibility
     */
    private ApexOperationSignature(Visibility visibility) {
        super(visibility);
    }


    @Override
    public int hashCode() {
        return code(visibility);
    }


    @Override
    public boolean equals(Object obj) {
        return obj == this;
    }


    private static int code(Visibility visibility) {
        return visibility.hashCode();
    }


    /**
     * Builds the signature of this node.
     *
     * @param node The method node
     *
     * @return The signature of the node
     */
    public static ApexOperationSignature of(ASTMethod node) {
        Visibility visibility = Visibility.get(node);
        int code = code(visibility);
        if (!POOL.containsKey(code)) {
            POOL.put(code, new ApexOperationSignature(visibility));
        }
        return POOL.get(code);
    }


}
