/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Collections;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;

import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.ast.compilation.Compilation;

public final class ASTApexFile extends AbstractApexNode<AstNode> implements RootNode {

    private final LanguageVersion languageVersion;
    private final String file;
    private Map<Integer, String> suppressMap = Collections.emptyMap();

    ASTApexFile(ParserTask task,
                AbstractApexNode<? extends Compilation> child) {
        super(child.getNode());
        this.languageVersion = task.getLanguageVersion();
        this.file = task.getFileDisplayName();
        addChild(child, 0);
        super.calculateLineNumbers(task.getTextDocument());
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    @Override
    public String getSourceCodeFile() {
        return file;
    }

    @Override
    public double getApexVersion() {
        return getNode().getDefiningType().getCodeUnitDetails().getVersion().getExternal();
    }

    public ApexNode<Compilation> getMainNode() {
        return (ApexNode<Compilation>) getChild(0);
    }

    @Override
    public @NonNull ASTApexFile getRoot() {
        return this;
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public Map<Integer, String> getNoPmdComments() {
        return suppressMap;
    }

    void setNoPmdComments(Map<Integer, String> suppressMap) {
        this.suppressMap = suppressMap;
    }
}
