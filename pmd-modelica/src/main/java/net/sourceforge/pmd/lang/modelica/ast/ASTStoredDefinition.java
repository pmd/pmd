/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.modelica.resolver.CompositeName;

/**
 * A representation of a Modelica source code file.
 */
public class ASTStoredDefinition extends AbstractModelicaNode implements RootNode {
    private boolean hasBOM = false;
    private LanguageVersion languageVersion;
    private String filename;

    ASTStoredDefinition(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptModelicaVisitor(ModelicaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    void markHasBOM() {
        hasBOM = true;
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return languageVersion;
    }

    @Override
    public String getSourceCodeFile() {
        return filename;
    }

    ASTStoredDefinition addTaskInfo(ParserTask languageVersion) {
        this.languageVersion = languageVersion.getLanguageVersion();
        this.filename = languageVersion.getFileDisplayName();
        return this;
    }


    /**
     * Returns whether this source file contains Byte Order Mark.
     */
    public boolean getHasBOM() {
        return hasBOM;
    }

    @Override
    public void jjtClose() {
        super.jjtClose();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getNumChildren(); ++i) {
            AbstractModelicaNode child = (AbstractModelicaNode) getChild(i);
            if (child instanceof ASTWithinClause) {
                if (sb.length() > 0) {
                    sb.append(CompositeName.NAME_COMPONENT_SEPARATOR);
                }
                sb.append(child.getImage());
            }
        }
        setImage(sb.toString());
    }
}
