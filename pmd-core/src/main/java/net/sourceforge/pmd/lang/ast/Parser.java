/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Objects;

import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * Produces an AST from a source file. Instances of this interface must
 * be stateless (which makes them trivially threadsafe).
 */
public interface Parser {

    /**
     * Parses an entire tree for this language. This may perform some
     * semantic analysis, like name resolution.
     *
     * @param task Description of the parsing task
     *
     * @return The root of the tree corresponding to the source code.
     *
     * @throws IllegalArgumentException If the language version of the
     *                                  parsing task is for an incorrect language
     * @throws FileAnalysisException    If any error occurs
     */
    RootNode parse(ParserTask task) throws FileAnalysisException;


    /**
     * Parameters passed to a parsing task.
     */
    final class ParserTask {

        private final TextDocument textDoc;
        private final SemanticErrorReporter reporter;
        private final LanguageProcessorRegistry lpRegistry;

        public ParserTask(TextDocument textDoc, SemanticErrorReporter reporter, LanguageProcessorRegistry lpRegistry) {
            this.textDoc = AssertionUtil.requireParamNotNull("Text document", textDoc);
            this.reporter = AssertionUtil.requireParamNotNull("reporter", reporter);
            this.lpRegistry = AssertionUtil.requireParamNotNull("lpRegistry", lpRegistry);
            Objects.requireNonNull(lpRegistry.getProcessor(textDoc.getLanguageVersion().getLanguage()));
        }


        public LanguageVersion getLanguageVersion() {
            return textDoc.getLanguageVersion();
        }

        /**
         * The display name for where the file comes from. This should
         * not be interpreted, it may not be a file-system path.
         */
        public String getFileDisplayName() {
            return textDoc.getDisplayName();
        }

        /**
         * The text document to parse.
         */
        public TextDocument getTextDocument() {
            return textDoc;
        }

        /**
         * The full text of the file to parse.
         */
        public String getSourceText() {
            return getTextDocument().getText().toString();
        }

        /**
         * The error reporter for semantic checks.
         */
        public SemanticErrorReporter getReporter() {
            return reporter;
        }

        public LanguageProcessorRegistry getLpRegistry() {
            return lpRegistry;
        }

        public LanguageProcessor getLanguageProcessor() {
            return lpRegistry.getProcessor(getLanguageVersion().getLanguage());
        }

        public ParserTask withTextDocument(TextDocument textDocument) {
            return new ParserTask(
                textDocument,
                this.reporter,
                this.lpRegistry
            );
        }
    }


}
