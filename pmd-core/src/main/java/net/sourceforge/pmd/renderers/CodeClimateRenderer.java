/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import com.google.gson.Gson;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

import static net.sourceforge.pmd.renderers.CodeClimateRule.CODECLIMATE_CATEGORIES;
import static net.sourceforge.pmd.renderers.CodeClimateRule.CODECLIMATE_REMEDIATION_MULTIPLIER;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Renderer for Code Climate JSON format
 */
public class CodeClimateRenderer extends AbstractIncrementingRenderer {
    public static final String NAME = "codeclimate";
    public static final int REMEDIATION_POINTS_DEFAULT = 50000;

    protected static final String EOL = System.getProperty("line.separator", "\n");
    // Note: required by https://github.com/codeclimate/spec/blob/master/SPEC.md
    protected static final String NULL_CHARACTER = "\u0000";

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
            writer.write(gson.toJson(asIssue(rv)) + NULL_CHARACTER + EOL);
        }
    }

    /**
     * Generate a CodeClimateIssue suitable for processing into JSON from the given RuleViolation.
     * @param rv RuleViolation to convert.
     * @return The generated issue.
     */
    private CodeClimateIssue asIssue(RuleViolation rv) {
    	Rule rule = rv.getRule();
        
    	CodeClimateIssue issue = new CodeClimateIssue();
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
        
        if(rule.hasDescriptor(CODECLIMATE_REMEDIATION_MULTIPLIER)) {
        	issue.remediation_points = rule.getProperty(CODECLIMATE_REMEDIATION_MULTIPLIER) * REMEDIATION_POINTS_DEFAULT;
        }
        
        if(rule.hasDescriptor(CODECLIMATE_CATEGORIES)) {
        	issue.categories = rule.getProperty(CODECLIMATE_CATEGORIES);
	    }
    
        return issue;
    }

    @Override
    public String defaultFileExtension() {
        return "json";
    }
}
