/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.signature;

/**
 * Signature mask for a field. Newly created masks cover any field.
 *
 * @author Clément Fournier
 */
public final class FieldSigMask extends JavaSigMask<JavaFieldSignature> {

    private boolean coverFinal = true;
    private boolean coverStatic = true;


    public FieldSigMask() {
        super();
    }


    /** Include final fields?. */
    public void coverFinal(boolean coverFinal) {
        this.coverFinal = coverFinal;
    }


    /** Include static fields?. */
    public void coverStatic(boolean coverStatic) {
        this.coverStatic = coverStatic;
    }


    @Override
    public boolean covers(JavaFieldSignature sig) {
        return super.covers(sig) && (coverFinal || !sig.isFinal) && (coverStatic || !sig.isStatic);
    }
}
