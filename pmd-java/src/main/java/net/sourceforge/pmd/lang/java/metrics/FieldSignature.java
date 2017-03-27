/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class FieldSignature extends Signature {
    
    public final Visibility visibility;
    public final boolean    isStatic;
    public final boolean    isFinal;

    public FieldSignature(Visibility visibility, boolean isStatic, boolean isFinal) {
        this.visibility = visibility;
        this.isStatic = isStatic;
        this.isFinal = isFinal;
    }

}
