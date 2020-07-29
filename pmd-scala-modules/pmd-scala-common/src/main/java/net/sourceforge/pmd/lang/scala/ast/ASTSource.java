/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.ast;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;

import scala.meta.Source;

/**
 * The ASTSource node implementation.
 */
public final class ASTSource extends AbstractScalaNode<Source> implements RootNode {

    private LanguageVersion languageVersion;
    private String filename;

    ASTSource(Source scalaNode) {
        super(scalaNode);
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }


    @Override
    public String getSourceCodeFile() {
        return filename;
    }

    void addTaskInfo(ParserTask languageVersion) {
        this.languageVersion = languageVersion.getLanguageVersion();
        this.filename = languageVersion.getFileDisplayName();
    }

    @Override
    protected <P, R> R acceptVisitor(ScalaParserVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
