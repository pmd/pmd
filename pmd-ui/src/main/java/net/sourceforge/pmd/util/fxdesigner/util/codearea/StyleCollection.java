/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.util.Comparator;
import java.util.TreeSet;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * @author Clément Fournier
 * @since 6.5.0
 */
public class StyleCollection extends TreeSet<NodeStyleSpan> {

    public StyleCollection() {
        super(Comparator.comparing(NodeStyleSpan::getNode, Comparator.comparingInt(Node::getBeginLine).thenComparing(Node::getBeginColumn)));
    }


    public static StyleCollection empty() {
        return new StyleCollection();
    }

}
