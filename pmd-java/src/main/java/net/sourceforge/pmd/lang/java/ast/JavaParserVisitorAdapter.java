/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * An adapter for {@link JavaParserVisitor}.
 *
 * @deprecated Use {@link JavaVisitorBase}
 */
@Deprecated
@DeprecatedUntil700
public class JavaParserVisitorAdapter extends JavaVisitorBase<Object, Object> implements JavaParserVisitor {

    @Override
    protected Object visitChildren(Node node, Object data) {
        super.visitChildren(node, data);
        return data;
    }

}
