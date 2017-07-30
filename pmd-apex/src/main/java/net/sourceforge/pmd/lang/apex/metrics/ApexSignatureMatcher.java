/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.metrics;

import net.sourceforge.pmd.lang.apex.ast.ApexQualifiedName;
import net.sourceforge.pmd.lang.apex.metrics.signature.ApexOperationSigMask;

/**
 * @author Clément Fournier
 */
public interface ApexSignatureMatcher {

    /**
     * Returns true if the signature of the operation designated by the qualified name is covered by the mask.
     *
     * @param qname   The operation to test
     * @param sigMask The signature mask to use
     *
     * @return True if the signature of the operation designated by the qualified name is covered by the mask
     */
    boolean hasMatchingSig(ApexQualifiedName qname, ApexOperationSigMask sigMask);
}
