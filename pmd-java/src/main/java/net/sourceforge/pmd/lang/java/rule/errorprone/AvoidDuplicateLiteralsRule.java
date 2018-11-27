/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.CharacterProperty;
import net.sourceforge.pmd.properties.FileProperty;
import net.sourceforge.pmd.properties.IntegerProperty;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.properties.StringProperty;

public class AvoidDuplicateLiteralsRule extends AbstractJavaRule {

    public static final IntegerProperty THRESHOLD_DESCRIPTOR 
            = IntegerProperty.named("maxDuplicateLiterals")
                             .desc("Max duplicate literals")
                             .range(1, 20).defaultValue(4).uiOrder(1.0f).build();

    public static final IntegerProperty MINIMUM_LENGTH_DESCRIPTOR = new IntegerProperty("minimumLength",
            "Minimum string length to check", 1, Integer.MAX_VALUE, 3, 1.5f);

    public static final BooleanProperty SKIP_ANNOTATIONS_DESCRIPTOR = new BooleanProperty("skipAnnotations",
            "Skip literals within annotations", false, 2.0f);

    public static final StringProperty EXCEPTION_LIST_DESCRIPTOR = new StringProperty("exceptionList",
            "Strings to ignore", null, 3.0f);

    public static final CharacterProperty SEPARATOR_DESCRIPTOR = new CharacterProperty("separator",
            "Ignore list separator", ',', 4.0f);

    public static final FileProperty EXCEPTION_FILE_DESCRIPTOR = new FileProperty("exceptionfile",
            "File containing strings to skip (one string per line), only used if ignore list is not set. "
            + "File must be UTF-8 encoded.", null, 5.0f);

    public static class ExceptionParser {

        private static final char ESCAPE_CHAR = '\\';
        private char delimiter;

        public ExceptionParser(char delimiter) {
            this.delimiter = delimiter;
        }

        public Set<String> parse(String s) {
            Set<String> result = new HashSet<>();
            StringBuilder currentToken = new StringBuilder();
            boolean inEscapeMode = false;
            for (int i = 0; i < s.length(); i++) {
                if (inEscapeMode) {
                    inEscapeMode = false;
                    currentToken.append(s.charAt(i));
                    continue;
                }
                if (s.charAt(i) == ESCAPE_CHAR) {
                    inEscapeMode = true;
                    continue;
                }
                if (s.charAt(i) == delimiter) {
                    result.add(currentToken.toString());
                    currentToken = new StringBuilder();
                } else {
                    currentToken.append(s.charAt(i));
                }
            }
            if (currentToken.length() > 0) {
                result.add(currentToken.toString());
            }
            return result;
        }
    }

    private Map<String, List<ASTLiteral>> literals = new HashMap<>();
    private Set<String> exceptions = new HashSet<>();
    private int minLength;

    public AvoidDuplicateLiteralsRule() {
        definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
        definePropertyDescriptor(MINIMUM_LENGTH_DESCRIPTOR);
        definePropertyDescriptor(SKIP_ANNOTATIONS_DESCRIPTOR);
        definePropertyDescriptor(EXCEPTION_LIST_DESCRIPTOR);
        definePropertyDescriptor(SEPARATOR_DESCRIPTOR);
        definePropertyDescriptor(EXCEPTION_FILE_DESCRIPTOR);
    }

    private LineNumberReader getLineReader() throws IOException {
        return new LineNumberReader(Files.newBufferedReader(getProperty(EXCEPTION_FILE_DESCRIPTOR).toPath(),
                StandardCharsets.UTF_8));
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
        literals.clear();

        if (getProperty(EXCEPTION_LIST_DESCRIPTOR) != null) {
            ExceptionParser p = new ExceptionParser(getProperty(SEPARATOR_DESCRIPTOR));
            exceptions = p.parse(getProperty(EXCEPTION_LIST_DESCRIPTOR));
        } else if (getProperty(EXCEPTION_FILE_DESCRIPTOR) != null) {
            exceptions = new HashSet<>();
            try (LineNumberReader reader = getLineReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    exceptions.add(line);
                }
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }

        minLength = 2 + getProperty(MINIMUM_LENGTH_DESCRIPTOR);

        super.visit(node, data);

        processResults(data);

        return data;
    }

    private void processResults(Object data) {

        int threshold = getProperty(THRESHOLD_DESCRIPTOR);

        for (Map.Entry<String, List<ASTLiteral>> entry : literals.entrySet()) {
            List<ASTLiteral> occurrences = entry.getValue();
            if (occurrences.size() >= threshold) {
                ASTLiteral first = occurrences.get(0);
                String rawImage = first.getEscapedStringLiteral();
                Object[] args = new Object[] { rawImage, Integer.valueOf(occurrences.size()),
                    Integer.valueOf(first.getBeginLine()), };
                addViolation(data, first, args);
            }
        }
    }

    @Override
    public Object visit(ASTLiteral node, Object data) {
        if (!node.isStringLiteral()) {
            return data;
        }
        String image = node.getImage();

        // just catching strings of 'minLength' chars or more (including the
        // enclosing quotes)
        if (image.length() < minLength) {
            return data;
        }

        // skip any exceptions
        if (exceptions.contains(image.substring(1, image.length() - 1))) {
            return data;
        }

        // Skip literals in annotations
        if (getProperty(SKIP_ANNOTATIONS_DESCRIPTOR) && node.getFirstParentOfType(ASTAnnotation.class) != null) {
            return data;
        }

        if (literals.containsKey(image)) {
            List<ASTLiteral> occurrences = literals.get(image);
            occurrences.add(node);
        } else {
            List<ASTLiteral> occurrences = new ArrayList<>();
            occurrences.add(node);
            literals.put(image, occurrences);
        }

        return data;
    }

    private static String checkFile(File file) {

        if (!file.exists()) {
            return "File '" + file.getName() + "' does not exist";
        }
        if (!file.canRead()) {
            return "File '" + file.getName() + "' cannot be read";
        }
        if (file.length() == 0) {
            return "File '" + file.getName() + "' is empty";
        }

        return null;
    }

    /**
     * @see PropertySource#dysfunctionReason()
     */
    @Override
    public String dysfunctionReason() {

        File file = getProperty(EXCEPTION_FILE_DESCRIPTOR);
        if (file != null) {
            String issue = checkFile(file);
            if (issue != null) {
                return issue;
            }

            String ignores = getProperty(EXCEPTION_LIST_DESCRIPTOR);
            if (StringUtils.isNotBlank(ignores)) {
                return "Cannot reference external file AND local values";
            }
        }

        return null;
    }
}
