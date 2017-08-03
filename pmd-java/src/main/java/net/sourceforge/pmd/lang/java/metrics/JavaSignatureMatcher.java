/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.JavaQualifiedName;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.metrics.signature.JavaOperationSigMask;

/**
 * Gathers the methods that the Java project mirror should make available to metrics during the computation.
 *
 * @author Clément Fournier
 */
public interface JavaSignatureMatcher {

    /**
     * Returns true if the signature of the operation designated by the qualified name is covered by the mask.
     *
     * @param qname   The operation to test
     * @param sigMask The signature mask to use
     *
     * @return True if the signature of the operation designated by the qualified name is covered by the mask
     */
    boolean hasMatchingSig(JavaQualifiedName qname, JavaOperationSigMask sigMask);


    /**
     * Returns true if the signature of the field designated by its name and the qualified name of its class is covered
     * by the mask.
     *
     * @param qname     The class of the field
     * @param fieldName The name of the field
     * @param sigMask   The signature mask to use
     *
     * @return True if the signature of the field is covered by the mask
     */
    boolean hasMatchingSig(JavaQualifiedName qname, String fieldName, JavaFieldSigMask sigMask);


}
