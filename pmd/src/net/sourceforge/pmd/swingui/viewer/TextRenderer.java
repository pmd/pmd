package net.sourceforge.pmd.swingui.viewer;

import java.util.Iterator;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.Renderer;

/**
 *
 * Converts the violations list into a text string for viewing.
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
class TextRenderer
{

    /**
     *******************************************************************************
     *
     * @param report
     *
     * @return Formatted text.
     */
    public String render(String fileName, Report report)
    {
        StringBuffer outputText = new StringBuffer(500);
        Iterator violations = report.iterator();

        outputText.append("Source File: ");
        outputText.append(fileName);
        outputText.append(System.getProperty("line.separator"));

        if (violations.hasNext() == false)
        {
            outputText.append("\nNo rule violations detected.");
        }

        while (violations.hasNext())
        {
            RuleViolation ruleViolation = (RuleViolation) violations.next();
            Rule rule = ruleViolation.getRule();

            //
            // Line Number
            //
            outputText.append(System.getProperty("line.separator"));
            outputText.append("Line: ");
            outputText.append(ruleViolation.getLine());
            outputText.append(System.getProperty("line.separator"));

            //
            // Rule Name
            //
            outputText.append("Rule Name: ");
            outputText.append(rule.getName());
            outputText.append(System.getProperty("line.separator"));

            //
            // Rule Message
            //
            String ruleMessage = ruleViolation.getDescription();

            if (ruleMessage == null)
            {
                ruleMessage = "";
            }
            else
            {
                ruleMessage = ruleMessage.replace('\n', ' ').trim();
            }

            outputText.append("Rule: ");
            outputText.append(ruleMessage);
            outputText.append(System.getProperty("line.separator"));

            //
            // Rule Description
            //
            String description = rule.getDescription();

            if (description == null)
            {
                description = "";
            }
            else
            {
                description = description.replace('\n', ' ').trim();
            }

            outputText.append("Description: ");
            outputText.append(description);
            outputText.append(System.getProperty("line.separator"));

            //
            // Rule Example
            //
            String example = rule.getExample();

            if ((example != null) && (example.length() > 0))
            {
                outputText.append("Example: ");
                outputText.append(example);
                outputText.append(System.getProperty("line.separator"));
            }
        }

        return outputText.toString();
    }
}