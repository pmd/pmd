/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static net.sourceforge.pmd.renderers.CodeClimateRule.CODECLIMATE_CATEGORIES;
import static net.sourceforge.pmd.renderers.CodeClimateRule.CODECLIMATE_REMEDIATION_MULTIPLIER;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

/**
 * Renderer for Code Climate JSON format
 */
public class CodeClimateRenderer extends AbstractIncrementingRenderer {
    public static final String NAME = "codeclimate";
    public static final String BODY_PLACEHOLDER = "REPLACE_THIS_WITH_MARKDOWN";
    public static final int REMEDIATION_POINTS_DEFAULT = 50000;
    public static final String[] CODECLIMATE_DEFAULT_CATEGORIES = new String[]{ "Style" };

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
            String json = gson.toJson(asIssue(rv));
            json = json.replaceAll(BODY_PLACEHOLDER, getBody(rv));
            writer.write(json + NULL_CHARACTER + PMD.EOL);
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
        issue.description = cleaned(rv.getDescription());
        issue.content = new CodeClimateIssue.Content(BODY_PLACEHOLDER);
        issue.location = getLocation(rv);
        
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
        
        issue.remediation_points = REMEDIATION_POINTS_DEFAULT;
        if(rule.hasDescriptor(CODECLIMATE_REMEDIATION_MULTIPLIER)) {
        	issue.remediation_points *= rule.getProperty(CODECLIMATE_REMEDIATION_MULTIPLIER);
        }
        
        if(rule.hasDescriptor(CODECLIMATE_CATEGORIES)) {
            Object[] categories = rule.getProperty(CODECLIMATE_CATEGORIES);
            issue.categories = new String[categories.length];
            for (int i = 0; i < categories.length; i++) {
                issue.categories[i] = String.valueOf(categories[i]);
            }
        }
        else {
        	issue.categories = CODECLIMATE_DEFAULT_CATEGORIES;
        }

        return issue;
    }

    @Override
    public String defaultFileExtension() {
        return "json";
    }
    
    private CodeClimateIssue.Location getLocation(RuleViolation rv) {
    	String pathWithoutCcRoot = StringUtils.removeStartIgnoreCase(rv.getFilename(), "/code/");
    	CodeClimateIssue.Location result = new CodeClimateIssue.Location(pathWithoutCcRoot, 
    																	 rv.getBeginLine(), 
    																	 rv.getEndLine());
    	return result;
    }
    
    private String cleaned(String original) {
    	String result = original.trim();
    	result = result.replaceAll("\\s+", " ");
    	result = result.replaceAll("\\s*[\\r\\n]+\\s*", "");
    	result = result.replaceAll("'", "`");
    	return result;
    }
    
    private String getBody(RuleViolation rv) {
    	Rule rule = rv.getRule();
    	
    	String result = "### Description" + PMD.EOL + 
    			      cleaned( rule.getDescription() );
    	
    	if(!rule.getExamples().isEmpty()) {
    		result += PMD.EOL + "### Example" + PMD.EOL;
    		
    		for(String snippet : rule.getExamples()) {
    			result += "```java " + PMD.EOL +
    					snippet + PMD.EOL +
    					"```  ";
    		}
    	}
    	
    	return result;
    }
}
