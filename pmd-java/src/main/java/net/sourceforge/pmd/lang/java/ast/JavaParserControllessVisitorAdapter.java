/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * AST visitor that doesn't take over control (doesn't drive the visit itself). That's wrapped into a {@link
 * JavaParserDecoratedVisitor} to implement a decorator pattern over a visitor.
 *
 * @author Clément Fournier
 * @since 6.0.0
 *
 * @deprecated Visitor decorators are deprecated because they lead to fragile code.
 */
@Deprecated
public class JavaParserControllessVisitorAdapter extends JavaParserVisitorAdapter implements JavaParserControllessVisitor {

    @Override
    public Object visit(JavaNode node, Object data) {
        return data;
    }

}
