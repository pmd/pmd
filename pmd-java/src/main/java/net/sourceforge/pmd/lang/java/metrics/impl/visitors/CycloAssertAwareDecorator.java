/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.visitors;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorDecorator;


/**
 * @author Clément Fournier
 * @since 6.0.0
 *
 * @deprecated Visitor decorators are deprecated because they lead to fragile code.
 *
 */
@Deprecated
public class CycloAssertAwareDecorator extends JavaParserVisitorDecorator {

    @Override
    public Object visit(ASTAssertStatement node, Object data) {
        ((MutableInt) data).add(2); // equivalent to if (condition) { throw .. }
        return data;
    }
}
