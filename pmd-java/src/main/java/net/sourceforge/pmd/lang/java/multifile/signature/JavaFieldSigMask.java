/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile.signature;

import net.sourceforge.pmd.lang.java.multifile.signature.JavaSignature.Visibility;

/**
 * Signature mask for a field. Newly created masks cover any field.
 *
 * @author Clément Fournier
 */
public final class JavaFieldSigMask extends JavaSigMask<JavaFieldSignature> {

    private boolean coverFinal = true;
    private boolean coverStatic = true;


    /**
     * Creates a field mask covering any field.
     */
    public JavaFieldSigMask() {
        // everything's initialized
    }


    /**
     * Sets the mask to cover final fields.
     *
     * @return this
     */
    public JavaFieldSigMask coverFinal() {
        this.coverFinal = true;
        return this;
    }


    /**
     * Forbid final fields.
     *
     * @return this
     */
    public JavaFieldSigMask forbidFinal() {
        coverFinal = false;
        return this;
    }


    /**
     * Sets the mask to cover static fields.
     *
     * @return this
     */
    public JavaFieldSigMask coverStatic() {
        this.coverStatic = true;
        return this;
    }


    /**
     * Forbid abstract operations.
     *
     * @return this
     */
    public JavaFieldSigMask forbidStatic() {
        coverStatic = false;
        return this;
    }


    @Override
    public JavaFieldSigMask coverAllVisibilities() {
        super.coverAllVisibilities();
        return this;
    }


    @Override
    public JavaFieldSigMask restrictVisibilitiesTo(Visibility... visibilities) {
        super.restrictVisibilitiesTo(visibilities);
        return this;
    }


    @Override
    public JavaFieldSigMask forbid(Visibility... visibilities) {
        super.forbid(visibilities);
        return this;
    }


    @Override
    public boolean covers(JavaFieldSignature sig) {
        return super.covers(sig) && (coverFinal || !sig.isFinal) && (coverStatic || !sig.isStatic);
    }
}
