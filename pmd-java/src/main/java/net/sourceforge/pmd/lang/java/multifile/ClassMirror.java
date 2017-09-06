/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;

/**
 * Represents a class for signature matching.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
interface ClassMirror {

    /**
     * Returns true if the signature of the operation designated by the qualified name is covered by the mask.
     *
     * @param opName  The operation to test
     * @param sigMask The signature mask to use
     *
     * @return True if the signature of the operation designated by the qualified name is covered by the mask
     */
    boolean hasMatchingOpSig(String opName, JavaOperationSigMask sigMask);


    /**
     * Returns true if the signature of the field designated by its name and the qualified name of its class is covered
     * by the mask.
     *
     * @param fieldName The name of the field
     * @param sigMask   The signature mask to use
     *
     * @return True if the signature of the field is covered by the mask
     */
    boolean hasMatchingFieldSig(String fieldName, JavaFieldSigMask sigMask);


    /**
     * Returns the number of operations matching the signature mask in the class.
     *
     * @param sigMask Signature mask
     *
     * @return The number of operations matching the signature mask
     */
    int countMatchingOpSigs(JavaOperationSigMask sigMask);


    /**
     * Returns the number of fields matching the signature mask in the class.
     *
     * @param sigMask Signature mask
     *
     * @return The number of fields matching the signature mask
     */
    int countMatchingFieldSigs(JavaFieldSigMask sigMask);

}
