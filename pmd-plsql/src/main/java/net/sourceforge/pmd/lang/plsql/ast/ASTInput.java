/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTInput extends AbstractPLSQLNode implements RootNode {

    private LanguageVersion languageVersion;

    ASTInput(int id) {
        super(id);
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    ASTInput setLanguageVersion(LanguageVersion languageVersion) {
        this.languageVersion = languageVersion;
        return this;
    }

    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getSourcecode() {
        return new StringBuilder(getText()).toString();
    }
}
