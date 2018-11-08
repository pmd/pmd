/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;


/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JVarReference extends JCodeReference<ASTVariableDeclaratorId> {


    boolean isFinal();

}
