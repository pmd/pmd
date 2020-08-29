/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile.signature;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;

/**
 * Signature mask for a field. Newly created masks cover any field.
 *
 * @author Clément Fournier
 */
@Deprecated
@DeprecatedUntil700
public final class JavaFieldSigMask extends JavaSigMask<JavaFieldSignature> {

    private boolean coverFinal = true;
    private boolean coverStatic = true;

    /** Include final fields?. */
    public void coverFinal(boolean coverFinal) {
        this.coverFinal = coverFinal;
    }


    public void coverFinal() {
        this.coverFinal = true;
    }


    public void forbidFinal() {
        this.coverFinal = false;
    }


    /** Include static fields?. */
    public void coverStatic(boolean coverStatic) {
        this.coverStatic = coverStatic;
    }


    public void coverStatic() {
        this.coverStatic = true;
    }


    public void forbidStatic() {
        this.coverStatic = false;
    }


    @Override
    public boolean covers(JavaFieldSignature sig) {
        return super.covers(sig) && (coverFinal || !sig.isFinal) && (coverStatic || !sig.isStatic);
    }
}
