/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.oom.visitor;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;

/**
 * @author Clément Fournier
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
        //TODO
        return o instanceof FieldSignature;
    }
    
    public FieldSignature buildFor(ASTFieldDeclaration node) {
        return new FieldSignature(Visibility.get(node), node.isStatic(), node.isFinal());
    }

    @Override
    public int hashCode(){
        //TODO
        return 0;
    }

}
