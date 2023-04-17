/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.apex.ApexLanguageProcessor;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.Parser;

import com.google.summit.SummitAST;
import com.google.summit.translation.Translate;
import com.google.summit.ast.CompilationUnit;

@InternalApi
@SuppressWarnings("PMD.DoNotUseJavaUtilLogging")
public final class ApexParser implements Parser {

    public ApexParser() {
        // Suppress INFO-level output
        Logger.getLogger(Translate.class.getName()).setLevel(Level.WARNING);
    }

    @Override
    public ASTApexFile parse(final ParserTask task) {
        CompilationUnit astRoot = null;

        PrintStream err = System.err; //NOPMD ok not to close; is save/restore pattern
        try {
            // Redirect System.err to suppress ANTLR warning about runtime/compilation version mismatch.
            // See: org.antlr.v4.runtime.RuntimeMetadata
            System.setErr(new PrintStream(new ByteArrayOutputStream()));

            astRoot = SummitAST.INSTANCE.parseAndTranslate(task.getTextDocument().getText().toString(), null);
        } finally {
            System.setErr(err);
        }

        if (astRoot == null) {
            throw new ParseException("Couldn't parse the source - there is not root node - Syntax Error??");
        }

        final ApexTreeBuilder treeBuilder = new ApexTreeBuilder(task, (ApexLanguageProcessor) task.getLanguageProcessor());
        return treeBuilder.buildTree(astRoot);
    }
}
