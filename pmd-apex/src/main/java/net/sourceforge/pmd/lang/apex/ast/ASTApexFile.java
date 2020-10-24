/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;


import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.ast.compilation.Compilation;
import com.nawforce.common.diagnostics.Issue;

public final class ASTApexFile extends AbstractApexNode<AstNode> implements RootNode {

    private Map<Integer, String> suppressMap = Collections.emptyMap();

    private String fileName;

    private ApexMultifileAnalysis multifileAnalysis;

    ASTApexFile(SourceCodePositioner source, AbstractApexNode<? extends Compilation> child) {
        super(child.getNode());
        addChild(child, 0);
        this.beginLine = 1;
        this.endLine = source.getLastLine();
        this.beginColumn = 1;
        this.endColumn = source.getLastLineColumn();
        child.setCoords(child.getBeginLine(), child.getBeginColumn(), source.getLastLine(), source.getLastLineColumn());
    }

    @Override
    public double getApexVersion() {
        return getNode().getDefiningType().getCodeUnitDetails().getVersion().getExternal();
    }

    public ApexNode<Compilation> getMainNode() {
        return (ApexNode<Compilation>) getChild(0);
    }

    @Override
    void calculateLineNumbers(SourceCodePositioner positioner) {
        this.beginLine = 1;
        this.beginColumn = 1;
        this.endLine = positioner.getLastLine();
        this.endColumn = positioner.getLastLineColumn();
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

    void setMultifileAnalysis(String fileName, ApexMultifileAnalysis multifileAnalysis) {
        this.fileName = fileName;
        this.multifileAnalysis = multifileAnalysis;
    }

    public List<Issue> getGlobalIssues() {
        if (multifileAnalysis != null) {
            return Collections.unmodifiableList(Arrays.asList(multifileAnalysis.getFileIssues(fileName)));
        } else {
            return Collections.emptyList();
        }
    }
}
