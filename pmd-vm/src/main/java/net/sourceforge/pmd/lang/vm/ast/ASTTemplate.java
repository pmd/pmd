/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.ast;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTTemplate extends AbstractVmNode implements RootNode {

    private LanguageVersion languageVersion;
    private String filename;

    public ASTTemplate(int id) {
        super(id);
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    @Override
    public String getSourceCodeFile() {
        return filename;
    }

    ASTTemplate addTaskInfo(ParserTask languageVersion) {
        this.languageVersion = languageVersion.getLanguageVersion();
        this.filename = languageVersion.getFileDisplayName();
        return this;
    }


    @Override
    public Object jjtAccept(VmParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
