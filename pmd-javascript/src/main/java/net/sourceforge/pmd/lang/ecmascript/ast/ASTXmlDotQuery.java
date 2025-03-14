/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.XmlDotQuery;

public final class ASTXmlDotQuery extends AbstractInfixEcmascriptNode<XmlDotQuery> {
    ASTXmlDotQuery(XmlDotQuery xmlDotQuery) {
        super(xmlDotQuery);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
