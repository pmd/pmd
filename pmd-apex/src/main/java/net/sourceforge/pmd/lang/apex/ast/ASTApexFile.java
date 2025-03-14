/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileAnalysis;
import net.sourceforge.pmd.lang.ast.AstInfo;
import net.sourceforge.pmd.lang.ast.Parser.ParserTask;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextRegion;

import com.google.summit.ast.CompilationUnit;
import io.github.apexdevtools.api.Issue;

public final class ASTApexFile extends AbstractApexNode.Single<CompilationUnit> implements RootNode {

    private final AstInfo<ASTApexFile> astInfo;
    private final @NonNull ApexMultifileAnalysis multifileAnalysis;

    ASTApexFile(ParserTask task,
                CompilationUnit compilationUnit,
                Map<Integer, String> suppressMap,
                @NonNull ApexLanguageProcessor apexLang) {
        super(compilationUnit);
        this.astInfo = new AstInfo<>(task, this).withSuppressMap(suppressMap);
        this.multifileAnalysis = apexLang.getMultiFileState();
        this.setRegion(TextRegion.fromOffsetLength(0, task.getTextDocument().getLength()));
    }

    @Override
    public AstInfo<ASTApexFile> getAstInfo() {
        return astInfo;
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
        FileId fileId = getAstInfo().getTextDocument().getFileId();
        return multifileAnalysis.getFileIssues(fileId.getAbsolutePath());
    }

    @Override
    public String getDefiningType() {
        // an apex file can contain only one top level type
        BaseApexClass baseApexClass = firstChild(BaseApexClass.class);
        if (baseApexClass != null) {
            return baseApexClass.getQualifiedName().toString();
        }
        return null;
    }
}
