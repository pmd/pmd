/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Objects;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.TextDocument;

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

        public ParserTask(TextDocument textDoc, SemanticErrorReporter reporter) {
            this.textDoc = Objects.requireNonNull(textDoc, "Text document was null");
            this.reporter = Objects.requireNonNull(reporter, "reporter was null");
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


    }


}
