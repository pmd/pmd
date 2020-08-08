/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTTypeDeclaration extends AbstractJavaTypeNode implements CanSuppressWarnings {

    @InternalApi
    @Deprecated
    public ASTTypeDeclaration(int id) {
        super(id);
    }

    @Override
    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        for (int i = 0; i < getNumChildren(); i++) {
            if (getChild(i) instanceof ASTAnnotation) {
                ASTAnnotation a = (ASTAnnotation) getChild(i);
                if (a.suppresses(rule)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
