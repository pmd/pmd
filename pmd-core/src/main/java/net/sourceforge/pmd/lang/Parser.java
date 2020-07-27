/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SemanticErrorReporter;

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
        private final String filepath;
        private final String sourceText;
        private final SemanticErrorReporter reporter;

        private final String commentMarker;


        public ParserTask(LanguageVersion lv, String filepath, String sourceText, SemanticErrorReporter reporter) {
            this(lv, filepath, sourceText, reporter, PMD.SUPPRESS_MARKER);
        }

        public ParserTask(LanguageVersion lv, String filepath, String sourceText, SemanticErrorReporter reporter, String commentMarker) {
            this.lv = lv;
            this.filepath = filepath;
            this.sourceText = sourceText;
            this.reporter = reporter;
            this.commentMarker = commentMarker;
        }


        public LanguageVersion getLanguageVersion() {
            return lv;
        }

        /**
         * The display name for where the file comes from. This should
         * not be interpreted, it may not be a file-system path.
         */
        public String getFileDisplayName() {
            return filepath;
        }

        /**
         * The full text of the file to parse.
         */
        public String getSourceText() {
            return sourceText;
        }

        /**
         * The error reporter for semantic checks.
         */
        public SemanticErrorReporter getReporter() {
            return reporter;
        }

        public String getCommentMarker() {
            return commentMarker;
        }
    }


}
