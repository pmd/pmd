/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @author Clément Fournier
 */
abstract class AbstractJavaPattern extends AbstractJavaTypeNode implements ASTPattern {

    AbstractJavaPattern(int i) {
        super(i);
    }

}
