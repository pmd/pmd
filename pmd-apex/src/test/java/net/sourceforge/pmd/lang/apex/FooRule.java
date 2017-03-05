/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Sample rule that detect any node with an image of "Foo". Used for testing.
 */
public class FooRule extends AbstractApexRule {

    @Override
    public Object visit(ApexNode<?> node, Object data) {
        if ("Foo".equals(node.getImage())) {
            addViolation(data, node);
        }
        return data;
    }
}
