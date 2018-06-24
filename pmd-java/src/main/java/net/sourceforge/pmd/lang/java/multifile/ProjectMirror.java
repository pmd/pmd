/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile;

import net.sourceforge.pmd.lang.java.multifile.signature.JavaFieldSigMask;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSigMask;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;

/**
 * Represents the analysed project to provide all rules with info about other classes.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
interface ProjectMirror {

    /**
     * Returns true if the signature of the operation designated by the qualified name is covered by the mask.
     *
     * @param qname   The operation to test
     * @param sigMask The signature mask to use
     *
     * @return True if the signature of the operation designated by the qualified name is covered by the mask
     */
    boolean hasMatchingSig(JavaOperationQualifiedName qname, JavaOperationSigMask sigMask);


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
    boolean hasMatchingSig(JavaTypeQualifiedName qname, String fieldName, JavaFieldSigMask sigMask);


    /**
     * Gets the class mirror corresponding to the qualified name.
     *
     * @param className The qualified name of the class.
     *
     * @return The class mirror
     */
    ClassMirror getClassMirror(JavaTypeQualifiedName className);

}
