/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression.convertToCanonicalQuery;

import com.google.summit.ast.expression.SoslExpression;

public final class ASTSoslExpression extends AbstractApexNode.Single<SoslExpression> {
    private final String canoncialQuery;

    ASTSoslExpression(SoslExpression soslExpression) {
        super(soslExpression);
        canoncialQuery = convertToCanonicalQuery(soslExpression.getQuery());
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the raw query as it appears in the source code.
     */
    public String getQuery() {
        return node.getQuery();
    }

    /**
     * Returns the query with the SOSL keywords normalized as uppercase.
     */
    public String getCanonicalQuery() {
        return canoncialQuery;
    }
}
