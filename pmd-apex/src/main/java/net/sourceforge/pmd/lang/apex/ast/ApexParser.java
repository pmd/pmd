/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;

import com.google.summit.SummitAST;
import com.google.summit.ast.CompilationUnit;
import com.google.summit.translation.Translate;

@SuppressWarnings("PMD.DoNotUseJavaUtilLogging")
public final class ApexParser implements Parser {

    // This is static - it keeps the Logger from being garbage collected
    // we want to configure the log level for this once.
    private static final Logger TRANSLATE_LOGGER = Logger.getLogger(Translate.class.getName());

    static {
        // Suppress INFO-level output
        TRANSLATE_LOGGER.setLevel(Level.WARNING);
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
