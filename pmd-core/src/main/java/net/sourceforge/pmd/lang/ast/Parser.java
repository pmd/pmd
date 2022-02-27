/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static net.sourceforge.pmd.internal.util.AssertionUtil.requireParamNotNull;

import java.util.Objects;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.properties.AbstractPropertySource;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.PropertySource;

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

        private final LanguageVersion lv;
        private final String filepath;
        private final String sourceText;
        private final SemanticErrorReporter reporter;
        private final ClassLoader auxclasspathClassLoader;

        private final PropertySource propertySource;

        public ParserTask(LanguageVersion lv, String filepath, String sourceText, SemanticErrorReporter reporter, ClassLoader auxclasspathClassLoader) {
            this.lv = requireParamNotNull("language version", lv);
            this.filepath = requireParamNotNull("filepath", filepath);
            this.sourceText = requireParamNotNull("sourceText", sourceText);
            this.reporter = requireParamNotNull("reporter", reporter);
            this.auxclasspathClassLoader = requireParamNotNull("auxclasspathClassLoader", auxclasspathClassLoader);

            this.propertySource = new ParserTaskProperties();
            propertySource.definePropertyDescriptor(COMMENT_MARKER);
        }

        public ParserTask(LanguageVersion lv, String filepath, String sourceText, SemanticErrorReporter reporter) {
            this(lv, filepath, sourceText, reporter, Parser.class.getClassLoader());
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

        @Deprecated // transitional until language properties are implemented
        public ClassLoader getAuxclasspathClassLoader() {
            return auxclasspathClassLoader;
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

        /**
         * The suppression marker for comments.
         */
        public @NonNull String getCommentMarker() {
            return getProperties().getProperty(COMMENT_MARKER);
        }


        private static final class ParserTaskProperties extends AbstractPropertySource {

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
