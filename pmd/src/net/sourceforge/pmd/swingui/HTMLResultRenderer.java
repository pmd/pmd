package net.sourceforge.pmd.swingui;

import java.io.StringWriter;
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
        writer.write("<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">");
        writer.write("<html>");
        writer.write("<head>");
        writer.write("<meta content=\"text/html; charset=iso-8859-1\">");
        writer.write("<title>PMD Analysis Results</title>");
        writer.write("</head>");

        //
        // Write the body.
        //
        writer.write("<body>");

        //
        // Write the name of the file that was analyzed.
        //
        writer.write("<h3><center><font color=\"blue\">PMD Analysis Results</font></center></h3>");
        writer.write("<b>Source File:</b> ");
        writer.write(fileName);
        writer.write("<br>");

        //
        // Create a table.
        //
        if (violations.hasNext() == false)
        {
            writer.write("<p>No rule violations detected.");
        }
        else
        {
            writer.write("<table bgcolor=\"#FFFFD9\" border>");

            //
            // Create the column headings.
            //
            writer.write("<tr>");
            writer.write("<th><b>Line<br>No.</b></th>");
            writer.write("<th><b>Rule</b></th>");
            writer.write("<th><b>Description</b></th>");
            writer.write("<th><b>Example</b></th>");
            writer.write("</tr>");

            while (violations.hasNext())
            {
                RuleViolation ruleViolation = (RuleViolation) violations.next();
                Rule rule = ruleViolation.getRule();

                //
                // Begin table row.
                //
                writer.write("<tr>");

                //
                // Line Number
                //
                writer.write("<td align=\"center\" valign=\"top\">");
                writer.write(String.valueOf(ruleViolation.getLine()));
                writer.write("</td>");

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

                writer.write("<td align=\"left\" valign=\"top\">");
                writer.write(ruleMessage);
                writer.write("</td>");

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

                writer.write("<td align=\"left\" valign=\"top\">");
                writer.write(description);
                writer.write("</td>");

                //
                // Rule Example
                //
                String example = rule.getExample();

                if ((example != null) && (example.length() > 0))
                {
                    writer.write("<td align=\"left\" valign=\"top\">");
                    writer.write("<pre>");
                    writer.write(example);
                    writer.write("</pre>");
                    writer.write("</td>");
                }

                //
                // End table row.
                //
                writer.write("</tr>");
            }

            writer.write("</table>");
        }

        //
        // Closeup.
        //
        writer.write("</body>");
        writer.write("</html>");

        return writer.toString();
    }
}