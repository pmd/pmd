/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.util.document.TextDocument;

/**
 * Produces an AST from a source file. Instances of this interface must
 * be stateless (which makes them trivially threadsafe).
 *
 * TODO
 *  - The reader + filename would be a TextDocument
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

        private final ParserTaskProperties propertySource;

        public ParserTask(TextDocument textDoc, SemanticErrorReporter reporter) {
            this(textDoc, reporter, new ParserTaskProperties());
        }

        private ParserTask(TextDocument textDoc, SemanticErrorReporter reporter, ParserTaskProperties source) {
            this.textDoc = Objects.requireNonNull(textDoc, "Text document was null");
            this.reporter = Objects.requireNonNull(reporter, "reporter was null");

            this.propertySource = new ParserTaskProperties(source);
        }

        public static final PropertyDescriptor<String> COMMENT_MARKER =
            PropertyFactory.stringProperty("suppressionCommentMarker")
                           .desc("deprecated! NOPMD")
                           .defaultValue(PMD.SUPPRESS_MARKER)
                           .build();

        @Deprecated // transitional until language properties are implemented
        public PropertySource getProperties() {
            return propertySource;
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

        /**
         * The suppression marker for comments.
         */
        public @NonNull String getCommentMarker() {
            return getProperties().getProperty(COMMENT_MARKER);
        }

        /**
         * Replace the text document with another.
         */
        public ParserTask withTextDocument(TextDocument doc) {
            return new ParserTask(doc, this.reporter, this.propertySource);
        }


        private static final class ParserTaskProperties extends AbstractPropertySource {

            ParserTaskProperties() {
                definePropertyDescriptor(COMMENT_MARKER);
            }

            ParserTaskProperties(ParserTaskProperties toCopy) {
                for (PropertyDescriptor<?> prop : toCopy.getPropertyDescriptors()) {
                    definePropertyDescriptor(prop);
                }
                toCopy.getOverriddenPropertyDescriptors().forEach(
                    prop -> copyProperty(prop, toCopy, this)
                );
            }

            static <T> void copyProperty(PropertyDescriptor<T> prop, PropertySource source, PropertySource target) {
                target.setProperty(prop, source.getProperty(prop));
            }

            @Override
            protected String getPropertySourceType() {
                return "ParserOptions";
            }

            @Override
            public String getName() {
                return "n/a";
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj) {
                    return true;
                }
                if (!(obj instanceof ParserTaskProperties)) {
                    return false;
                }
                final ParserTaskProperties that = (ParserTaskProperties) obj;
                return Objects.equals(getPropertiesByPropertyDescriptor(),
                                      that.getPropertiesByPropertyDescriptor());
            }

            @Override
            public int hashCode() {
                return getPropertiesByPropertyDescriptor().hashCode();
            }
        }
    }


}
