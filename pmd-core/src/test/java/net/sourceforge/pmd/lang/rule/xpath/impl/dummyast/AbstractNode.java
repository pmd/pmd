/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl.dummyast;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.document.Chars;

// This class is package private
// and provides the implementation for getValue().
// This method is not accessible from outside this package,
// it is made available in the subclass ConcreteNode.
class AbstractNode extends DummyNode {

    AbstractNode() {

    }

    Chars getValue() {
        return Chars.wrap("actual_value");
    }
}
