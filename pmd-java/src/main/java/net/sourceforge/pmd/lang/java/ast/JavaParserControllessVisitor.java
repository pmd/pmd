/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Visitor which doesn't take over control on the AST visit (it doesn't explore its children). This is used in our
 * modified version of the decorator pattern on an AST visitor.
 *
 * @author Clément Fournier
 * @since 6.0.0
 *
 * @see JavaParserDecoratedVisitor
 * @see JavaParserVisitorDecorator
 *
 * @deprecated Visitor decorators are deprecated because they lead to fragile code.
 */
@Deprecated
public interface JavaParserControllessVisitor extends JavaParserVisitor {
}
