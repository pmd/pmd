/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * @author Clément Fournier
 * @since 6.2.0
 */
abstract class AbstractTypeBodyDeclaration extends AbstractJavaNode implements JavaNode {

    AbstractTypeBodyDeclaration(int id) {
        super(id);
    }

}
