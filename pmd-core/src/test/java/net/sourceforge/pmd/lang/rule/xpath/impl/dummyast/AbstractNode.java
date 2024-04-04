/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl.dummyast;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.document.Chars;

// This class is package private
// and provides the implementation for getValue(). This
// class is the DeclaringClass for that method.
class AbstractNode extends DummyNode implements ValueNode {

    AbstractNode() {

    }

    @Override
    public final Chars getValue() {
        return Chars.wrap("actual_value");
    }
}
