/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Produces an AST from a source file. Instances of this interface must
 * be stateless (which makes them trivially threadsafe).
 *
 * TODO
 *  - Ideally ParserOptions would be an argument to ::parse
 *  - ::parse would also take some more parameters, eg an error collector
 *  - The reader + filename would be a TextDocument
 *
 * @author Pieter_Van_Raemdonck - Application Engineers NV/SA - www.ae.be
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

        private final LanguageVersion lv;
        private final TextDocument textDoc;
        private final SemanticErrorReporter reporter;

        private final String commentMarker;


        public ParserTask(LanguageVersion lv, TextDocument textDoc, SemanticErrorReporter reporter) {
            this(lv, textDoc, reporter, PMD.SUPPRESS_MARKER);
        }

        public ParserTask(LanguageVersion lv, TextDocument textDoc, SemanticErrorReporter reporter, String commentMarker) {
            this.lv = Objects.requireNonNull(lv, "lv was null");
            this.textDoc = Objects.requireNonNull(textDoc, "Text document was null");
            this.reporter = Objects.requireNonNull(reporter, "reporter was null");
            this.commentMarker = Objects.requireNonNull(commentMarker, "commentMarker was null");
        }


        public LanguageVersion getLanguageVersion() {
            return lv;
        }

        /**
         * The display name for where the file comes from. This should
         * not be interpreted, it may not be a file-system path.
         */
        public String getFileDisplayName() {
            return textDoc.getFileName();
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

        /**
         * The suppression marker for comments.
         */
        public @NonNull String getCommentMarker() {
            return commentMarker;
        }
    }


}
