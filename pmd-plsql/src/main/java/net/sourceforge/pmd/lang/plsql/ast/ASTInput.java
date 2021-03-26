/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;

public final class ASTInput extends AbstractPLSQLNode implements RootNode {

    private AstInfo<ASTInput> astInfo;

    ASTInput(int id) {
        super(id);
    }

    @Override
    public AstInfo<ASTInput> getAstInfo() {
        return astInfo;
    }

    ASTInput addTaskInfo(ParserTask task) {
        this.astInfo = new AstInfo<>(task, this);
        return this;
    }


    @Override
    protected <P, R> R acceptPlsqlVisitor(PlsqlVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getSourcecode() {
        return getAstInfo().getSourceText();
    }

    private int excludedRangesCount = 0;
    private int excludedLinesCount = 0;

    /**
     * Let the user know that a range of lines were excluded from parsing.
     *
     * @param first First line of the excluded line range (1-based).
     * @param last Last line  of the excluded line range (1-based).
    */
    void addExcludedLineRange(int first, int last) {
        excludedLinesCount += last - first + 1;
        excludedRangesCount += 1;
    }

    public int getExcludedLinesCount() {
        return excludedLinesCount;
    }

    public int getExcludedRangesCount() {
        return excludedRangesCount;
    }
}
