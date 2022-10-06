/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTStandardCondition extends AbstractApexNode.Single<Node> {

    @Deprecated
    @InternalApi
    public ASTStandardCondition(Node standardCondition) {
        super(standardCondition);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
