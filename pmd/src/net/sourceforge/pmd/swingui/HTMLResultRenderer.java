package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

import javax.swing.UIManager;
import java.io.StringWriter;
import java.util.Iterator;

/**
 *
 * Converts the violations list into a text string for viewing.
 *
 * @author Donald A. Leckie
 * @since August 27, 2002
 * @version $Revision$, $Date$
 */
class HTMLResultRenderer
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
        StringWriter writer = new StringWriter(5000);
        Iterator violations = report.iterator();

        //
        // Write HTML header.
        //
        writer.write("<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n");
        writer.write("<html>\n");
        writer.write("<head>\n");
        writer.write("<meta content=\"text/html; charset=iso-8859-1\">\n");
        writer.write("<title>PMD Analysis Results</title>\n");
        writer.write("</head>\n");

        //
        // Write the body.
        //
        writer.write("<body>\n");

        //
        // Write the name of the file that was analyzed.
        //
        writer.write("<h3><center><font color=\"blue\">PMD Analysis Results</font></center></h3>\n");
        writer.write("<b>Source File:</b> ");
        writer.write(fileName);
        writer.write("\n<br>\n");

        //
        // Create a table.
        //
        if (violations.hasNext() == false)
        {
            writer.write("<p>No rule violations detected.\n");
        }
        else
        {
            writer.write("<table bgcolor=\"" + UIManager.getColor("PMDCream") + "\" border>\n");

            //
            // Create the column headings.
            //
            writer.write("<tr>\n");
            writer.write("<th><b>Line<br>No.</b></th>\n");
            writer.write("<th><b>Rule</b></th>\n");
            writer.write("<th><b>Description</b></th>\n");
            writer.write("<th><b>Example</b></th>\n");
            writer.write("</tr>\n");

            while (violations.hasNext())
            {
                RuleViolation ruleViolation = (RuleViolation) violations.next();
                Rule rule = ruleViolation.getRule();

                //
                // Begin table row.
                //
                writer.write("<tr>\n");

                //
                // Line Number
                //
                writer.write("<td align=\"center\" valign=\"top\">\n");
                writer.write(String.valueOf(ruleViolation.getLine()));
                writer.write("\n</td>\n");

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

                writer.write("<td align=\"left\" valign=\"top\">\n");
                writer.write(ruleMessage);
                writer.write("\n</td>\n");

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

                writer.write("<td align=\"left\" valign=\"top\">\n");
                writer.write(description);
                writer.write("\n</td>\n");

                //
                // Rule Example
                //
                String example = rule.getExample();

                if ((example != null) && (example.length() > 0))
                {
                    writer.write("<td align=\"left\" valign=\"top\">\n");
                    writer.write("<pre>\n");
                    writer.write(example);
                    writer.write("\n</pre>\n");
                    writer.write("</td>\n");
                }

                //
                // End table row.
                //
                writer.write("</tr>\n");
            }

            writer.write("</table>\n");
        }

        //
        // Closeup.
        //
        writer.write("</body>\n");
        writer.write("</html>\n");

        return writer.toString();
    }
}