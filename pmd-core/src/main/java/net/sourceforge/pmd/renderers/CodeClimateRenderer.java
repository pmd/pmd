/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import com.google.gson.Gson;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Renderer for Code Climate JSON format
 */
public class CodeClimateRenderer extends AbstractIncrementingRenderer {
    public static final String NAME = "codeclimate";

    protected static final String EOL = System.getProperty("line.separator", "\n");

    public CodeClimateRenderer() {
        super(NAME, "Code Climate integration.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void renderFileViolations(Iterator<RuleViolation> violations) throws IOException {
        Writer writer = getWriter();
        Gson gson = new Gson();
        while (violations.hasNext()) {
            RuleViolation rv = violations.next();
            writer.write(gson.toJson(makeIssue(rv)) + EOL);
        }
    }

    /**
     * Generate a CodeClimateIssue suitable for processing into JSON from the given RuleViolation.
     * @param rv RuleViolation to convert.
     * @return The generated issue.
     */
    private CodeClimateIssue makeIssue(RuleViolation rv) {
        CodeClimateIssue issue = new CodeClimateIssue();
        Rule rule = rv.getRule();
        issue.check_name = rule.getName();
        issue.description = rv.getDescription();
        issue.content = new CodeClimateIssue.Content(rule.getDescription());
        issue.location = new CodeClimateIssue.Location(rv.getFilename(), rv.getBeginLine(), rv.getEndLine());
        switch(rule.getPriority()) {
            case HIGH:
                issue.severity = "critical";
                break;
            case MEDIUM_HIGH:
            case MEDIUM:
            case MEDIUM_LOW:
                issue.severity = "normal";
                break;
            case LOW:
                issue.severity = "info";
                break;
        }
        return issue;
    }

    @Override
    public String defaultFileExtension() {
        return "json";
    }
}
