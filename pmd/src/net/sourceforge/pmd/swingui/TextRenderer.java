package net.sourceforge.pmd.swingui;

import java.util.Iterator;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

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
     * The end of line string for this machine.
     */
    protected String EOL = System.getProperty("line.separator", "\n");

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
        outputText.append(EOL);

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
            outputText.append(EOL);
            outputText.append("Line: ");
            outputText.append(ruleViolation.getLine());
            outputText.append(EOL);

            //
            // Rule Name
            //
            outputText.append("Rule Name: ");
            outputText.append(rule.getName());
            outputText.append(EOL);

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
            outputText.append(EOL);

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
            outputText.append(EOL);

            //
            // Rule Example
            //
            String example = rule.getExample();

            if ((example != null) && (example.length() > 0))
            {
                outputText.append("Example: ");
                outputText.append(example);
                outputText.append(EOL);
            }
        }

        return outputText.toString();
    }
}