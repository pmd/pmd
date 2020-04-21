/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import net.sourceforge.pmd.lang.ast.TextAvailableNode;
import net.sourceforge.pmd.lang.ast.impl.GenericNode;


public interface JjtreeNode<N extends JjtreeNode<N>> extends GenericNode<N>, TextAvailableNode {


    JavaccToken getFirstToken();

    JavaccToken getLastToken();

}
