package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;

import javax.swing.UIManager;
import java.awt.Color;
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

    private boolean m_reportNoViolations;
    private StringWriter m_writer;

    /**
     *******************************************************************************
     *
     */
    protected void beginRendering(boolean reportNoViolations)
    {
        m_reportNoViolations = reportNoViolations;
        m_writer = new StringWriter(25000);

        //
        // Write HTML header.
        //
        m_writer.write("<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n");
        m_writer.write("<html>\n");
        m_writer.write("<head>\n");
        m_writer.write("<meta content=\"text/html; charset=iso-8859-1\">\n");
        m_writer.write("<title>PMD Analysis Results</title>\n");
        m_writer.write("</head>\n");

        //
        // Write the body.
        //
        m_writer.write("<body>\n");
    }

    /**
     *******************************************************************************
     *
     * @return HTML text.
     */
    protected String endRendering()
    {
        m_writer.write("</body>\n");
        m_writer.write("</html>\n");

        return m_writer.toString();
    }

    /**
     *******************************************************************************
     *
     * @param fileName
     * @param report
     */
    public void render(String fileName, Report report)
    {
        Iterator violations = report.iterator();

        if (violations.hasNext() == false)
        {
            if (m_reportNoViolations)
            {
                //
                // Write the name of the file that was analyzed.
                //
                m_writer.write("<b>Source File:</b> ");
                m_writer.write(fileName);
                m_writer.write("\n<p>\n");
                m_writer.write("No rule violations detected.<br>\n");
            }

            return;
        }

        //
        // Write the name of the file that was analyzed.
        //
        m_writer.write("<b>Source File:</b> ");
        m_writer.write(fileName);
        m_writer.write("\n<br>\n");

        Color pmdGray = UIManager.getColor("pmdGray");
        String hexValue = Integer.toHexString(pmdGray.getRGB());

        if (hexValue.startsWith("0x"))
        {
            hexValue = hexValue.substring(2);
        }

        if (hexValue.length() > 6)
        {
            hexValue = hexValue.substring(hexValue.length() - 6);
        }

        m_writer.write("<table bgcolor=\"#" + hexValue + "\" border>\n");

        //
        // Create the column headings.
        //
        m_writer.write("<tr>\n");
        m_writer.write("<th><b>Line<br>No.</b></th>\n");
        m_writer.write("<th><b>Rule</b></th>\n");
        m_writer.write("<th><b>Priority</b></th>\n");
        m_writer.write("<th><b>Description</b></th>\n");
        m_writer.write("<th><b>Example</b></th>\n");
        m_writer.write("</tr>\n");

        while (violations.hasNext())
        {
            RuleViolation ruleViolation = (RuleViolation) violations.next();
            Rule rule = ruleViolation.getRule();

            //
            // Begin table row.
            //
            m_writer.write("<tr>\n");

            //
            // Line Number
            //
            m_writer.write("<td align=\"center\" valign=\"top\">\n");
            m_writer.write("<font size=\"3\">\n");
            m_writer.write(String.valueOf(ruleViolation.getLine()));
            m_writer.write("\n</font>\n");
            m_writer.write("</td>\n");

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
                removeNewLineCharacters(ruleMessage);
            }

            m_writer.write("<td align=\"left\" valign=\"top\">\n");
            m_writer.write("<font size=\"3\">\n");
            m_writer.write(ruleMessage);
            m_writer.write("\n</font>\n");
            m_writer.write("</td>\n");

            //
            // Rule Priority
            //
            m_writer.write("<td align=\"left\" valign=\"top\">\n");
            m_writer.write("<font size=\"3\">\n");
            m_writer.write(rule.getPriorityName());
            m_writer.write("\n</font>\n");
            m_writer.write("</td>\n");

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
                removeNewLineCharacters(description);
            }

            m_writer.write("<td align=\"left\" valign=\"top\">\n");
            m_writer.write("<font size=\"3\">\n");
            m_writer.write(description);
            m_writer.write("\n</font>\n");
            m_writer.write("</td>\n");

            //
            // Rule Example
            //
            String example = rule.getExample();

            if ((example != null) && (example.length() > 0))
            {
                StringBuffer buffer = new StringBuffer(example);

                for (int n = buffer.length() - 1; n >= 0; n--)
                {
                    if (buffer.charAt(n) == '\n')
                    {
                        buffer.deleteCharAt(n);
                    }
                    else
                    {
                        break;
                    }
                }

                example = buffer.toString();

                m_writer.write("<td align=\"left\" valign=\"top\">\n");
                m_writer.write("<pre>\n");
                m_writer.write("<font size=\"-1\">");
                m_writer.write(example);
                m_writer.write("</font>");
                m_writer.write("\n</pre>\n");
                m_writer.write("</td>\n");
            }

            //
            // End table row.
            //
            m_writer.write("</tr>\n");
        }

        m_writer.write("</table>\n");
        m_writer.write("<p><p>\n");
    }

    /**
     ******************************************************************************
     *
     * @param text
     *
     * @return
     */
    private String removeNewLineCharacters(String text)
    {
        char[] chars = text.trim().toCharArray();
        int startIndex = 0;

        for (int n = 0; n < chars.length; n++)
        {
            if ((chars[n] != ' ') && (chars[n] != '\n'))
            {
                startIndex = n;
                break;
            }
        }

        int lastIndex = chars.length - 1;

        for (int n = lastIndex; n >= 0; n--)
        {
            if ((chars[n] != ' ') && (chars[n] != '\n'))
            {
                lastIndex = n;
                break;
            }
        }

        return String.valueOf(chars, startIndex, lastIndex + 1);
    }
}