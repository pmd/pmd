/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTCompilationUnit extends AbstractVfNode implements RootNode {

    private LanguageVersion languageVersion;

    ASTCompilationUnit(int id) {
        super(id);
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    ASTCompilationUnit setLanguageVersion(LanguageVersion languageVersion) {
        this.languageVersion = languageVersion;
        return this;
    }

    @Override
    public Object jjtAccept(VfParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
