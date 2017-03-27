/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.metrics;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class FieldSignature extends Signature {
    
    public final boolean isStatic;
    public final boolean isFinal;
    
    public FieldSignature(Visibility visibility, boolean isStatic, boolean isFinal) {
        super(visibility);
        this.isStatic = isStatic;
        this.isFinal = isFinal;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o instanceof FieldSignature) {
            // TODO
            return true;
        }
        return false;
    }
    
    public FieldSignature buildFor(ASTFieldDeclaration node) {
        return new FieldSignature(getVisibility(node), node.isStatic(), node.isFinal());
    }

}
