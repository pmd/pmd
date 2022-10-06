/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import org.apache.commons.lang3.reflect.FieldUtils;

public final class ASTTypeWhenBlock extends AbstractApexNode.Single<Node> {


    ASTTypeWhenBlock(Node node) {
        super(node);
    }

    public String getType() {
        // return String.valueOf(node.getTypeRef());
        // TODO(b/239648780)
        return null;
    }

    public String getName() {
        // unfortunately the name is not exposed...
        try {
            return String.valueOf(FieldUtils.readDeclaredField(node, "name", true));
        } catch (IllegalArgumentException | ReflectiveOperationException e) {
            return null;
        }
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
