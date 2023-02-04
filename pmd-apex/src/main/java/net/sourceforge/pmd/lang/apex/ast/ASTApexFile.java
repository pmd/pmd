/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.net.URI;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.document.TextRegion;

import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.ast.compilation.Compilation;
import com.nawforce.common.diagnostics.Issue;

public final class ASTApexFile extends AbstractApexNode<AstNode> implements RootNode {

    private final AstInfo<ASTApexFile> astInfo;
    private final @NonNull ApexMultifileAnalysis multifileAnalysis;

    ASTApexFile(ParserTask task,
                Compilation jorjeNode,
                Map<Integer, String> suppressMap,
                @NonNull ApexLanguageProcessor apexLang) {
        super(jorjeNode);
        this.astInfo = new AstInfo<>(task, this).withSuppressMap(suppressMap);
        this.multifileAnalysis = apexLang.getMultiFileState();
        this.setRegion(TextRegion.fromOffsetLength(0, task.getTextDocument().getLength()));
    }

    @Override
    public AstInfo<ASTApexFile> getAstInfo() {
        return astInfo;
    }

    @Override
    public double getApexVersion() {
        return getNode().getDefiningType().getCodeUnitDetails().getVersion().getExternal();
    }

    public ASTUserClassOrInterface<?> getMainNode() {
        return (ASTUserClassOrInterface<?>) getChild(0);
    }

    @Override
    public @NonNull ASTApexFile getRoot() {
        return this;
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public List<Issue> getGlobalIssues() {
        String filename = getAstInfo().getTextDocument().getPathId();
        if (filename.length() > 7 && "file://".equalsIgnoreCase(filename.substring(0, 7))) {
            URI uri = URI.create(filename);
            filename = Paths.get(uri).toString();
        }
        return multifileAnalysis.getFileIssues(filename);
    }
}
