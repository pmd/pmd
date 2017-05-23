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

    public static FieldSignature buildFor(ASTFieldDeclaration node) {
        return new FieldSignature(Visibility.get(node), node.isStatic(), node.isFinal());
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FieldSignature) {
            FieldSignature f = (FieldSignature) o;
            return super.equals(o) && f.isFinal == isFinal && f.isStatic == isStatic;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 16 + (isStatic ? 1 : 0) * 32 + (isFinal ? 1 : 0);
    }
}
