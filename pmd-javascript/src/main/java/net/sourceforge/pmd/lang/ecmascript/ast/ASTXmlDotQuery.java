/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlDotQuery;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTXmlDotQuery extends AbstractInfixEcmascriptNode<XmlDotQuery> {
    @Deprecated
    @InternalApi
    public ASTXmlDotQuery(XmlDotQuery xmlDotQuery) {
        super(xmlDotQuery);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
