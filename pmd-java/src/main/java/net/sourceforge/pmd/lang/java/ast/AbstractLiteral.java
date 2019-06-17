/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @author Clément Fournier
 */
class AbstractLiteral extends AbstractJavaExpr implements ASTLiteral {

    AbstractLiteral(int i) {
        super(i);
    }

    AbstractLiteral(JavaParser p, int i) {
        super(p, i);
    }
}
