/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

abstract class AbstractStatement extends AbstractJavaNode implements ASTStatement {

    AbstractStatement(int id) {
        super(id);
    }

}
