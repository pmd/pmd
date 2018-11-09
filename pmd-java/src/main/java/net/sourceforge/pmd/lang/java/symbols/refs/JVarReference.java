/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;


/**
 * Reference to a value, ie {@linkplain JLocalVarReference local variable} or {@linkplain JFieldReference field}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JVarReference extends JCodeReference<ASTVariableDeclaratorId> {


    boolean isFinal();

}
