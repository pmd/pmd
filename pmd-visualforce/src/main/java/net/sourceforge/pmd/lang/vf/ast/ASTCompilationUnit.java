/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTCompilationUnit extends AbstractVfNode implements RootNode {

    private LanguageVersion languageVersion;
    private String filename;

    ASTCompilationUnit(int id) {
        super(id);
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    ASTCompilationUnit addTaskInfo(ParserTask languageVersion) {
        this.languageVersion = languageVersion.getLanguageVersion();
        this.filename = languageVersion.getFileDisplayName();
        return this;
    }

    @Override
    protected <P, R> R acceptVfVisitor(VfVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
