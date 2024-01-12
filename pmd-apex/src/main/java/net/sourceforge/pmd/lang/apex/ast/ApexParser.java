/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;

import com.google.summit.SummitAST;
import com.google.summit.ast.CompilationUnit;
import com.google.summit.translation.Translate;

@InternalApi
@SuppressWarnings("PMD.DoNotUseJavaUtilLogging")
public final class ApexParser implements Parser {

    static {
        AntlrVersionCheckSuppression.initApexLexer();
    }

    public ApexParser() {
        // Suppress INFO-level output
        Logger.getLogger(Translate.class.getName()).setLevel(Level.WARNING);
    }

    @Override
    public ASTApexFile parse(final ParserTask task) {
        CompilationUnit astRoot = null;
        try {
            astRoot = SummitAST.INSTANCE.parseAndTranslate(task.getFileId().getOriginalPath(), task.getTextDocument().getText().toString(), null);
        } catch (SummitAST.ParseException e) {
            throw new ParseException(e);
        }

        assert astRoot != null;

        final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(task, (ApexLanguageProcessor) task.getLanguageProcessor());
        return treeBuilder.buildTree(astRoot);
    }
}
